package com.petshop.backend.service.vendor.impl;

import com.petshop.backend.common.OrderStatus;
import com.petshop.backend.common.PayoutStatus;
import com.petshop.backend.config.AppProperties;
import com.petshop.backend.dto.request.vendor.CreatePayoutRequest;
import com.petshop.backend.dto.request.vendor.VendorBankDetailsRequest;
import com.petshop.backend.dto.response.vendor.VendorBankDetailsResponse;
import com.petshop.backend.dto.response.vendor.VendorFinanceStatsResponse;
import com.petshop.backend.dto.response.vendor.VendorPayoutResponse;
import com.petshop.backend.entity.order.Order;
import com.petshop.backend.entity.product.Store;
import com.petshop.backend.entity.user.User;
import com.petshop.backend.entity.vendor.VendorBankDetails;
import com.petshop.backend.entity.vendor.VendorPayout;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.repository.order.OrderItemRepository;
import com.petshop.backend.repository.order.OrderRepository;
import com.petshop.backend.repository.product.StoreRepository;
import com.petshop.backend.repository.user.UserRepository;
import com.petshop.backend.repository.vendor.VendorBankDetailsRepository;
import com.petshop.backend.repository.vendor.VendorPayoutRepository;
import com.petshop.backend.security.CurrentUser;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.vendor.VendorFinanceService;
import com.petshop.backend.util.MaskUtils;
import com.petshop.backend.util.MoneyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;

@Service
public class VendorFinanceServiceImpl implements VendorFinanceService {

    private final VendorBankDetailsRepository bankDetailsRepository;
    private final VendorPayoutRepository payoutRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final BigDecimal commissionRate;

    public VendorFinanceServiceImpl(VendorBankDetailsRepository bankDetailsRepository,
                                    VendorPayoutRepository payoutRepository,
                                    OrderRepository orderRepository,
                                    OrderItemRepository orderItemRepository,
                                    StoreRepository storeRepository,
                                    UserRepository userRepository,
                                    AppProperties properties) {
        this.bankDetailsRepository = bankDetailsRepository;
        this.payoutRepository = payoutRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
        this.commissionRate = properties.getFinance().getCommissionRate();
    }

    @Override
    @Transactional(readOnly = true)
    public VendorFinanceStatsResponse getFinanceStats(UserPrincipal currentUser) {
        Long vendorId = CurrentUser.require(currentUser).getId();

        List<Order> delivered = orderRepository.findByVendorIdAndStatus(vendorId, OrderStatus.DELIVERED);
        List<Order> inProgress = orderRepository.findByVendorIdAndStatusIn(vendorId, OrderStatus.ACTIVE);
        BigDecimal gross = sum(delivered);
        BigDecimal commission = MoneyUtils.scale(gross.multiply(commissionRate));
        BigDecimal net = gross.subtract(commission);
        BigDecimal pendingBalance = MoneyUtils.scale(sum(inProgress).multiply(BigDecimal.ONE.subtract(commissionRate)));

        List<VendorPayout> payouts = payoutRepository.findByVendorIdOrderByCreatedAtDesc(vendorId);
        BigDecimal withdrawn = payouts.stream()
                .filter(p -> p.getStatus() == PayoutStatus.APPROVED || p.getStatus() == PayoutStatus.COMPLETED)
                .map(VendorPayout::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pendingPayouts = payouts.stream()
                .filter(p -> p.getStatus() == PayoutStatus.PENDING)
                .map(VendorPayout::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal available = net.subtract(withdrawn).subtract(pendingPayouts).max(BigDecimal.ZERO);

        long unitsSold = orderItemRepository.sumQuantityByVendorAndStatuses(vendorId,
                EnumSet.of(OrderStatus.PENDING, OrderStatus.PROCESSING, OrderStatus.SHIPPING, OrderStatus.DELIVERED));

        return VendorFinanceStatsResponse.builder()
                .totalRevenue(MoneyUtils.scale(gross))
                .commissionRate(commissionRate)
                .commissionAmount(commission)
                .netEarnings(MoneyUtils.scale(net))
                .availableBalance(MoneyUtils.scale(available))
                .pendingBalance(pendingBalance)
                .pendingPayouts(MoneyUtils.scale(pendingPayouts))
                .totalWithdrawn(MoneyUtils.scale(withdrawn))
                .totalSalesCount(unitsSold)
                .completedOrdersCount((long) delivered.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorPayoutResponse> getPayoutHistory(UserPrincipal currentUser) {
        return payoutRepository.findByVendorIdOrderByCreatedAtDesc(CurrentUser.require(currentUser).getId())
                .stream().map(VendorFinanceServiceImpl::toResponse).toList();
    }

    @Override
    @Transactional
    public VendorPayoutResponse createPayoutRequest(CreatePayoutRequest request, UserPrincipal currentUser) {
        CurrentUser.requireApproved(currentUser);
        Long vendorId = currentUser.getId();
        VendorBankDetails saved = bankDetailsRepository.findByVendorId(vendorId).orElse(null);
        String iban = request.getIban() != null ? request.getIban() : saved != null ? saved.getIban() : null;
        if (iban == null || iban.isBlank()) {
            throw new BadRequestException("BANK_DETAILS_REQUIRED", "Add your bank details (IBAN) before requesting a payout");
        }
        BigDecimal available = getFinanceStats(currentUser).getAvailableBalance();
        if (request.getAmount().compareTo(available) > 0) {
            throw new BadRequestException("INSUFFICIENT_BALANCE", "Requested amount exceeds the available balance of " + available + " AZN");
        }
        VendorPayout payout = payoutRepository.save(VendorPayout.builder()
                .vendorId(vendorId)
                .amount(MoneyUtils.scale(request.getAmount()))
                .iban(iban.replace(" ", "").toUpperCase())
                .bankName(request.getBankName() != null ? request.getBankName() : saved != null ? saved.getBankName() : null)
                .accountHolder(request.getAccountHolder() != null ? request.getAccountHolder()
                        : saved != null && saved.getAccountHolder() != null ? saved.getAccountHolder() : currentUser.getFullName())
                .status(PayoutStatus.PENDING)
                .build());
        return toResponse(payout);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorBankDetailsResponse getBankDetails(UserPrincipal currentUser) {
        Long vendorId = CurrentUser.require(currentUser).getId();
        return bankDetailsRepository.findByVendorId(vendorId)
                .map(VendorFinanceServiceImpl::toResponse)
                .orElseGet(() -> VendorBankDetailsResponse.builder()
                        .vendorId(vendorId)
                        .voen(storeRepository.findFirstByOwnerIdOrderByIdAsc(vendorId).map(Store::getVoen).orElse(null))
                        .build());
    }

    @Override
    @Transactional
    public VendorBankDetailsResponse updateBankDetails(VendorBankDetailsRequest request, UserPrincipal currentUser) {
        Long vendorId = CurrentUser.require(currentUser).getId();
        VendorBankDetails details = bankDetailsRepository.findByVendorId(vendorId)
                .orElseGet(() -> VendorBankDetails.builder().vendorId(vendorId).build());
        details.setIban(request.getIban().replace(" ", "").toUpperCase());
        details.setBankName(request.getBankName().trim());
        details.setAccountHolder(request.getAccountHolder() != null && !request.getAccountHolder().isBlank()
                ? request.getAccountHolder().trim()
                : userRepository.findById(vendorId).map(User::getFullName).orElse(null));
        details.setVoen(request.getVoen());
        VendorBankDetails saved = bankDetailsRepository.save(details);
        // Keep the store's VÖEN in sync (shown in the vendor sidebar).
        storeRepository.findFirstByOwnerIdOrderByIdAsc(vendorId).ifPresent(store -> {
            store.setVoen(request.getVoen());
            storeRepository.save(store);
        });
        return toResponse(saved);
    }

    private static BigDecimal sum(List<Order> orders) {
        return orders.stream().map(o -> MoneyUtils.nz(o.getTotalAmount())).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    static VendorPayoutResponse toResponse(VendorPayout p) {
        return VendorPayoutResponse.builder()
                .id(p.getId())
                .vendorId(p.getVendorId())
                .amount(p.getAmount())
                .iban(MaskUtils.maskIban(p.getIban()))
                .bankName(p.getBankName())
                .accountHolder(p.getAccountHolder())
                .status(p.getStatus() == null ? null : p.getStatus().name())
                .adminNote(p.getAdminNote())
                .createdAt(p.getCreatedAt())
                .processedAt(p.getProcessedAt())
                .build();
    }

    private static VendorBankDetailsResponse toResponse(VendorBankDetails d) {
        return VendorBankDetailsResponse.builder()
                .id(d.getId())
                .vendorId(d.getVendorId())
                .iban(d.getIban())
                .bankName(d.getBankName())
                .accountHolder(d.getAccountHolder())
                .voen(d.getVoen())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
