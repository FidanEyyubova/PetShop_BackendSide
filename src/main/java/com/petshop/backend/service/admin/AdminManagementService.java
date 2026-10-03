package com.petshop.backend.service.admin;

import com.petshop.backend.common.PayoutStatus;
import com.petshop.backend.common.Roles;
import com.petshop.backend.common.UserStatus;
import com.petshop.backend.dto.request.admin.UpdatePayoutStatusRequest;
import com.petshop.backend.dto.request.admin.UpdateStoreVerificationRequest;
import com.petshop.backend.dto.request.admin.UpdateUserStatusRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.product.StoreResponse;
import com.petshop.backend.dto.response.user.UserResponse;
import com.petshop.backend.dto.response.vendor.VendorPayoutResponse;
import com.petshop.backend.entity.product.Product;
import com.petshop.backend.entity.product.Store;
import com.petshop.backend.entity.user.User;
import com.petshop.backend.entity.vendor.VendorPayout;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.ConflictException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.repository.product.StoreRepository;
import com.petshop.backend.repository.user.UserRepository;
import com.petshop.backend.repository.vendor.VendorPayoutRepository;
import com.petshop.backend.service.notification.EmailService;
import com.petshop.backend.service.product.StoreService;
import com.petshop.backend.service.user.RefreshTokenService;
import com.petshop.backend.service.user.UserMapper;
import com.petshop.backend.util.MaskUtils;
import com.petshop.backend.util.PageUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Seller approval, account suspension, payout processing and store verification. */
@Service
public class AdminManagementService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final VendorPayoutRepository payoutRepository;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final UserMapper userMapper;
    private final StoreService storeService;

    public AdminManagementService(UserRepository userRepository, ProductRepository productRepository,
                                  StoreRepository storeRepository, VendorPayoutRepository payoutRepository,
                                  RefreshTokenService refreshTokenService, EmailService emailService,
                                  UserMapper userMapper, StoreService storeService) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.payoutRepository = payoutRepository;
        this.refreshTokenService = refreshTokenService;
        this.emailService = emailService;
        this.userMapper = userMapper;
        this.storeService = storeService;
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> users(String role, String status, String q, int page, int size) {
        String roleFilter = role == null || role.isBlank() ? null : Roles.normalize(role);
        UserStatus statusFilter = status == null || status.isBlank() ? null : UserStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            p.add(cb.isNull(root.get("deletedAt")));
            if (roleFilter != null) p.add(cb.equal(root.get("role"), roleFilter));
            if (statusFilter != null) p.add(cb.equal(root.get("status"), statusFilter));
            if (q != null && !q.isBlank()) {
                String pattern = "%" + MaskUtils.escapeLike(q.trim().toLowerCase(Locale.ROOT)) + "%";
                p.add(cb.or(cb.like(cb.lower(root.get("fullName")), pattern, '\\'), cb.like(cb.lower(root.get("email")), pattern, '\\')));
            }
            return cb.and(p.toArray(Predicate[]::new));
        };
        Page<User> result = userRepository.findAll(spec, PageUtils.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return PageResponse.of(result, userMapper::toResponse);
    }

    /**
     * Approves / rejects sellers and suspends accounts. Suspension revokes all sessions and hides a vendor's products;
     * re-activation does not republish them automatically.
     */
    @Transactional
    public UserResponse updateUserStatus(Long userId, UpdateUserStatusRequest request) {
        User user = userRepository.findById(userId)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (Roles.ROLE_ADMIN.equals(Roles.normalize(user.getRole()))) {
            throw new ConflictException("ADMIN_ACCOUNT", "Admin accounts cannot be changed here");
        }
        UserStatus next = UserStatus.valueOf(request.getStatus().trim().toUpperCase(Locale.ROOT));
        UserStatus previous = user.getStatus();
        user.setStatus(next);
        userRepository.save(user);

        if (next == UserStatus.SUSPENDED || next == UserStatus.REJECTED) {
            refreshTokenService.revokeAll(user.getId());
            List<Product> products = productRepository.findAll((root, q, cb) -> cb.and(
                    cb.equal(root.get("vendorId"), user.getId()), cb.isTrue(root.get("isActive"))));
            products.forEach(p -> p.setIsActive(false));
            productRepository.saveAll(products);
        }
        if (previous != next) {
            String body = switch (next) {
                case ACTIVE -> "Your PetShop account has been approved. You can now publish on the platform.";
                case REJECTED -> "Your PetShop seller application was not approved.";
                case SUSPENDED -> "Your PetShop account has been suspended.";
                case PENDING_APPROVAL -> "Your PetShop account is waiting for review.";
            };
            if (request.getReason() != null && !request.getReason().isBlank()) body += "\n\nReason: " + request.getReason().trim();
            emailService.send(user.getEmail(), "PetShop account status: " + next.name().toLowerCase().replace('_', ' '), body);
        }
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public PageResponse<VendorPayoutResponse> payouts(String status, int page, int size) {
        var pageable = PageUtils.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PayoutStatus s = PayoutStatus.parse(status);
        Page<VendorPayout> result = s == null ? payoutRepository.findAll(pageable) : payoutRepository.findByStatus(s, pageable);
        return PageResponse.of(result, AdminManagementService::toPayout);
    }

    /** PENDING -> APPROVED -> COMPLETED, or PENDING/APPROVED -> REJECTED (the amount returns to the balance). */
    @Transactional
    public VendorPayoutResponse updatePayoutStatus(Long id, UpdatePayoutStatusRequest request) {
        VendorPayout payout = payoutRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payout", "id", id));
        PayoutStatus next = PayoutStatus.parse(request.getStatus());
        PayoutStatus current = payout.getStatus();
        boolean allowed = switch (current) {
            case PENDING -> next == PayoutStatus.APPROVED || next == PayoutStatus.REJECTED || next == PayoutStatus.COMPLETED;
            case APPROVED -> next == PayoutStatus.COMPLETED || next == PayoutStatus.REJECTED;
            case COMPLETED, REJECTED -> false;
        };
        if (next == null || !allowed) {
            throw new BadRequestException("INVALID_STATUS_TRANSITION", "Cannot change payout from " + current + " to " + next);
        }
        payout.setStatus(next);
        payout.setAdminNote(request.getNote());
        if (next == PayoutStatus.COMPLETED || next == PayoutStatus.REJECTED) payout.setProcessedAt(LocalDateTime.now());
        return toPayout(payoutRepository.save(payout));
    }

    @Transactional
    public StoreResponse updateStoreVerification(Long storeId, UpdateStoreVerificationRequest request) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new ResourceNotFoundException("Store", "id", storeId));
        store.setVerified(request.getVerified());
        if (request.getBadgeText() != null) store.setBadgeText(request.getBadgeText().isBlank() ? null : request.getBadgeText().trim());
        if (request.getResponseRate() != null) store.setResponseRate(request.getResponseRate());
        storeRepository.save(store);
        return storeService.getStore(String.valueOf(storeId));
    }

    private static VendorPayoutResponse toPayout(VendorPayout p) {
        return VendorPayoutResponse.builder()
                .id(p.getId()).vendorId(p.getVendorId()).amount(p.getAmount())
                .iban(p.getIban()).bankName(p.getBankName()).accountHolder(p.getAccountHolder())
                .status(p.getStatus().name()).adminNote(p.getAdminNote())
                .createdAt(p.getCreatedAt()).processedAt(p.getProcessedAt()).build();
    }
}
