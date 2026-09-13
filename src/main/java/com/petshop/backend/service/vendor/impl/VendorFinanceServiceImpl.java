package com.petshop.backend.service.vendor.impl;

import com.petshop.backend.dto.request.vendor.CreatePayoutRequest;
import com.petshop.backend.dto.request.vendor.VendorBankDetailsRequest;
import com.petshop.backend.dto.response.vendor.VendorBankDetailsResponse;
import com.petshop.backend.dto.response.vendor.VendorFinanceStatsResponse;
import com.petshop.backend.dto.response.vendor.VendorPayoutResponse;
import com.petshop.backend.entity.order.Order;
import com.petshop.backend.entity.order.OrderItem;
import com.petshop.backend.entity.product.Product;
import com.petshop.backend.entity.vendor.VendorBankDetails;
import com.petshop.backend.entity.vendor.VendorPayout;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.repository.order.OrderItemRepository;
import com.petshop.backend.repository.order.OrderRepository;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.repository.vendor.VendorBankDetailsRepository;
import com.petshop.backend.repository.vendor.VendorPayoutRepository;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.vendor.VendorFinanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VendorFinanceServiceImpl implements VendorFinanceService {

    private final VendorBankDetailsRepository bankDetailsRepository;
    private final VendorPayoutRepository payoutRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public VendorFinanceServiceImpl(
            VendorBankDetailsRepository bankDetailsRepository,
            VendorPayoutRepository payoutRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository) {
        this.bankDetailsRepository = bankDetailsRepository;
        this.payoutRepository = payoutRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public VendorFinanceStatsResponse getFinanceStats(UserPrincipal currentUser) {
        Long vendorId = currentUser.getId();

        List<Product> vendorProducts = productRepository.findByVendorId(vendorId);
        Set<Long> vendorProductIds = vendorProducts.stream().map(Product::getId).collect(Collectors.toSet());

        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalSalesCount = 0;
        int completedOrdersCount = 0;

        if (!vendorProductIds.isEmpty()) {
            List<OrderItem> orderItems = orderItemRepository.findAll().stream()
                    .filter(item -> vendorProductIds.contains(item.getProductId()))
                    .collect(Collectors.toList());

            totalSalesCount = orderItems.size();

            for (OrderItem item : orderItems) {
                totalRevenue = totalRevenue.add(item.getSubtotal() != null ? item.getSubtotal() : BigDecimal.ZERO);
            }

            Set<Long> vendorOrderIds = orderItems.stream().map(OrderItem::getOrderId).collect(Collectors.toSet());
            List<Order> vendorOrders = orderRepository.findAllById(vendorOrderIds);

            completedOrdersCount = (int) vendorOrders.stream()
                    .filter(o -> "DELIVERED".equalsIgnoreCase(o.getStatus()) || "COMPLETED".equalsIgnoreCase(o.getStatus()))
                    .count();
        }

        List<VendorPayout> payouts = payoutRepository.findByVendorIdOrderByCreatedAtDesc(vendorId);

        BigDecimal completedPayoutsSum = payouts.stream()
                .filter(p -> "COMPLETED".equalsIgnoreCase(p.getStatus()) || "APPROVED".equalsIgnoreCase(p.getStatus()))
                .map(VendorPayout::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pendingPayoutsSum = payouts.stream()
                .filter(p -> "PENDING".equalsIgnoreCase(p.getStatus()))
                .map(VendorPayout::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal availableBalance = totalRevenue.subtract(completedPayoutsSum).subtract(pendingPayoutsSum);
        if (availableBalance.compareTo(BigDecimal.ZERO) < 0) {
            availableBalance = BigDecimal.ZERO;
        }

        return new VendorFinanceStatsResponse(
                totalRevenue,
                availableBalance,
                pendingPayoutsSum,
                totalSalesCount,
                completedOrdersCount
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorPayoutResponse> getPayoutHistory(UserPrincipal currentUser) {
        Long vendorId = currentUser.getId();
        return payoutRepository.findByVendorIdOrderByCreatedAtDesc(vendorId).stream()
                .map(p -> new VendorPayoutResponse(
                        p.getId(),
                        p.getVendorId(),
                        p.getAmount(),
                        p.getIban(),
                        p.getBankName(),
                        p.getAccountHolder(),
                        p.getStatus(),
                        p.getCreatedAt(),
                        p.getProcessedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VendorPayoutResponse createPayoutRequest(CreatePayoutRequest request, UserPrincipal currentUser) {
        Long vendorId = currentUser.getId();

        VendorFinanceStatsResponse stats = getFinanceStats(currentUser);
        if (request.getAmount().compareTo(stats.getAvailableBalance()) > 0) {
            throw new BadRequestException("Requested amount exceeds available balance: " + stats.getAvailableBalance());
        }

        VendorPayout payout = new VendorPayout();
        payout.setVendorId(vendorId);
        payout.setAmount(request.getAmount());
        payout.setIban(request.getIban());
        payout.setBankName(request.getBankName());
        payout.setAccountHolder(request.getAccountHolder());
        payout.setStatus("PENDING");

        VendorPayout saved = payoutRepository.save(payout);

        return new VendorPayoutResponse(
                saved.getId(),
                saved.getVendorId(),
                saved.getAmount(),
                saved.getIban(),
                saved.getBankName(),
                saved.getAccountHolder(),
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getProcessedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public VendorBankDetailsResponse getBankDetails(UserPrincipal currentUser) {
        Long vendorId = currentUser.getId();
        VendorBankDetails bankDetails = bankDetailsRepository.findByVendorId(vendorId)
                .orElseGet(() -> new VendorBankDetails(null, vendorId, "", "", "", null));

        return new VendorBankDetailsResponse(
                bankDetails.getId(),
                bankDetails.getVendorId(),
                bankDetails.getIban(),
                bankDetails.getBankName(),
                bankDetails.getAccountHolder(),
                bankDetails.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public VendorBankDetailsResponse updateBankDetails(VendorBankDetailsRequest request, UserPrincipal currentUser) {
        Long vendorId = currentUser.getId();
        VendorBankDetails bankDetails = bankDetailsRepository.findByVendorId(vendorId)
                .orElseGet(() -> {
                    VendorBankDetails bd = new VendorBankDetails();
                    bd.setVendorId(vendorId);
                    return bd;
                });

        bankDetails.setIban(request.getIban());
        bankDetails.setBankName(request.getBankName());
        bankDetails.setAccountHolder(request.getAccountHolder());

        VendorBankDetails saved = bankDetailsRepository.save(bankDetails);

        return new VendorBankDetailsResponse(
                saved.getId(),
                saved.getVendorId(),
                saved.getIban(),
                saved.getBankName(),
                saved.getAccountHolder(),
                saved.getUpdatedAt()
        );
    }
}