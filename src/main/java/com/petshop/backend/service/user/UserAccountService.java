package com.petshop.backend.service.user;

import com.petshop.backend.common.OrderStatus;
import com.petshop.backend.common.Roles;
import com.petshop.backend.dto.request.user.DeleteAccountRequest;
import com.petshop.backend.dto.request.user.NotificationPreferencesRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.user.AccountDeletionResponse;
import com.petshop.backend.dto.response.user.NotificationPreferencesResponse;
import com.petshop.backend.dto.response.user.UserStatsResponse;
import com.petshop.backend.entity.product.Product;
import com.petshop.backend.entity.service.PetService;
import com.petshop.backend.entity.user.AccountDeletion;
import com.petshop.backend.entity.user.User;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.ConflictException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.content.ReviewRepository;
import com.petshop.backend.repository.order.CartItemRepository;
import com.petshop.backend.repository.order.OrderRepository;
import com.petshop.backend.repository.order.WishlistRepository;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.repository.service.PetServiceRepository;
import com.petshop.backend.repository.user.AccountDeletionRepository;
import com.petshop.backend.repository.user.UserAddressRepository;
import com.petshop.backend.repository.user.UserRepository;
import com.petshop.backend.security.CurrentUser;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.util.PageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class UserAccountService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final WishlistRepository wishlistRepository;
    private final ReviewRepository reviewRepository;
    private final UserAddressRepository addressRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;
    private final PetServiceRepository serviceRepository;
    private final AccountDeletionRepository deletionRepository;

    public UserAccountService(UserRepository userRepository, OrderRepository orderRepository,
                              CartItemRepository cartItemRepository, WishlistRepository wishlistRepository,
                              ReviewRepository reviewRepository, UserAddressRepository addressRepository,
                              RefreshTokenService refreshTokenService, PasswordEncoder passwordEncoder,
                              ProductRepository productRepository, PetServiceRepository serviceRepository,
                              AccountDeletionRepository deletionRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.wishlistRepository = wishlistRepository;
        this.reviewRepository = reviewRepository;
        this.addressRepository = addressRepository;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.productRepository = productRepository;
        this.serviceRepository = serviceRepository;
        this.deletionRepository = deletionRepository;
    }

    @Transactional(readOnly = true)
    public NotificationPreferencesResponse getPreferences(UserPrincipal currentUser) {
        return toPreferences(load(currentUser));
    }

    @Transactional
    public NotificationPreferencesResponse updatePreferences(NotificationPreferencesRequest request, UserPrincipal currentUser) {
        User user = load(currentUser);
        if (request.getOrderUpdates() != null) user.setNotifyOrders(request.getOrderUpdates());
        if (request.getPromotions() != null) user.setNotifyPromotions(request.getPromotions());
        if (request.getBlogUpdates() != null) user.setNotifyBlog(request.getBlogUpdates());
        return toPreferences(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserStatsResponse getStats(UserPrincipal currentUser) {
        Long userId = CurrentUser.require(currentUser).getId();
        return UserStatsResponse.builder()
                .activeOrders(orderRepository.countByBuyerIdAndStatusIn(userId, OrderStatus.ACTIVE))
                .totalOrders(orderRepository.countByBuyerId(userId))
                .cartItems(cartItemRepository.sumQuantityByUserId(userId))
                .wishlistItems(wishlistRepository.countByUserId(userId))
                .reviews(reviewRepository.countByUserId(userId))
                .addresses(addressRepository.countByUserId(userId))
                .build();
    }

    /**
     * Soft-deletes the account: personal data is anonymised, the email is released for re-registration,
     * sessions, cart, wishlist and addresses are removed. Orders and reviews stay for vendors' records.
     * The password is checked when the account has one (Google sign-in accounts have none); the reason is logged for admins.
     */
    @Transactional
    public void deleteAccount(DeleteAccountRequest request, UserPrincipal currentUser) {
        User user = load(currentUser);
        if (user.getPassword() != null && (request.getPassword() == null
                || !passwordEncoder.matches(request.getPassword(), user.getPassword()))) {
            throw new BadRequestException("WRONG_PASSWORD", "Password is incorrect");
        }
        if (currentUser.isAdmin()) {
            throw new ConflictException("ADMIN_ACCOUNT", "Admin accounts cannot be deleted from the profile");
        }
        if (hasOrdersInProgress(user.getId())) {
            throw new ConflictException("ACTIVE_ORDERS", "You have orders in progress; delete the account after they are completed or cancelled");
        }
        hideSellerItems(user.getId());
        logDeletion(user, AccountDeletion.BY_SELF, request.getReason(), request.getComment());
        anonymise(user);
    }

    /**
     * Admin deletion of any non-admin account, with the same soft delete as the self-service one: the email is
     * released, so the person can register again with it. A seller's products / services are hidden first.
     * Refused (409) while the account still has orders in progress as a buyer or as a vendor.
     */
    @Transactional
    public void deleteAccountByAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (Roles.ROLE_ADMIN.equals(Roles.normalize(user.getRole()))) {
            throw new ConflictException("ADMIN_ACCOUNT", "Admin accounts cannot be deleted");
        }
        if (hasOrdersInProgress(userId)) {
            throw new ConflictException("ACTIVE_ORDERS", "This account has orders in progress; delete it after they are completed or cancelled");
        }
        hideSellerItems(userId);
        logDeletion(user, AccountDeletion.BY_ADMIN, null, null);
        anonymise(user);
    }

    /** Deleted accounts, newest first; {@code deletedBy} (SELF or ADMIN) filters by who deleted them. */
    @Transactional(readOnly = true)
    public PageResponse<AccountDeletionResponse> deletions(String deletedBy, int page, int size) {
        var pageable = PageUtils.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AccountDeletion> result = deletedBy == null || deletedBy.isBlank()
                ? deletionRepository.findAll(pageable)
                : deletionRepository.findByDeletedBy(deletedBy.trim().toUpperCase(Locale.ROOT), pageable);
        return PageResponse.of(result, UserAccountService::toDeletionResponse);
    }

    /** Orders in progress as a buyer or, for vendors, as the selling store. */
    private boolean hasOrdersInProgress(Long userId) {
        return orderRepository.countByBuyerIdAndStatusIn(userId, OrderStatus.ACTIVE) > 0
                || orderRepository.countByVendorIdAndStatusIn(userId, OrderStatus.ACTIVE) > 0;
    }

    /** Records who the account belonged to before anonymisation, and why it was deleted. */
    private void logDeletion(User user, String deletedBy, String reason, String comment) {
        deletionRepository.save(AccountDeletion.builder()
                .userId(user.getId())
                .role(user.getRole())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .reason(reason == null || reason.isBlank() ? null : reason.trim().toUpperCase(Locale.ROOT))
                .comment(comment == null || comment.isBlank() ? null : comment.trim())
                .deletedBy(deletedBy)
                .build());
    }

    private static AccountDeletionResponse toDeletionResponse(AccountDeletion d) {
        return AccountDeletionResponse.builder()
                .id(d.getId())
                .userId(d.getUserId())
                .role(d.getRole())
                .fullName(d.getFullName())
                .email(d.getEmail())
                .reason(d.getReason())
                .comment(d.getComment())
                .deletedBy(d.getDeletedBy())
                .createdAt(d.getCreatedAt())
                .build();
    }

    /** Hides a vendor's products and a provider's services so nothing of a deleted seller stays on sale. */
    private void hideSellerItems(Long userId) {
        List<Product> products = productRepository.findAll((root, q, cb) -> cb.and(
                cb.equal(root.get("vendorId"), userId), cb.isTrue(root.get("isActive"))));
        products.forEach(p -> p.setIsActive(false));
        productRepository.saveAll(products);
        List<PetService> services = serviceRepository.findByProviderIdAndActiveTrueOrderByIdAsc(userId);
        services.forEach(s -> s.setActive(false));
        serviceRepository.saveAll(services);
    }

    /** Signs the user out everywhere, removes personal lists and anonymises the profile (frees the email). */
    private void anonymise(User user) {
        refreshTokenService.revokeAll(user.getId());
        cartItemRepository.deleteByUserId(user.getId());
        wishlistRepository.deleteByUserId(user.getId());
        addressRepository.deleteByUserId(user.getId());

        user.setEmail("deleted-" + user.getId() + "@deleted.petshop.local");
        user.setFullName("Deleted user");
        user.setPhone(null);
        user.setAvatarUrl(null);
        user.setCityAddress(null);
        user.setPassword(null);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private User load(UserPrincipal currentUser) {
        CurrentUser.require(currentUser);
        return userRepository.findById(currentUser.getId())
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));
    }

    private NotificationPreferencesResponse toPreferences(User user) {
        return NotificationPreferencesResponse.builder()
                .orderUpdates(!Boolean.FALSE.equals(user.getNotifyOrders()))
                .promotions(!Boolean.FALSE.equals(user.getNotifyPromotions()))
                .blogUpdates(Boolean.TRUE.equals(user.getNotifyBlog()))
                .build();
    }
}
