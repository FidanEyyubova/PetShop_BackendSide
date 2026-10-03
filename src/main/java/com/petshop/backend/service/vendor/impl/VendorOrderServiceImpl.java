package com.petshop.backend.service.vendor.impl;

import com.petshop.backend.common.OrderStatus;
import com.petshop.backend.dto.request.vendor.UpdateOrderStatusRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.order.OrderResponse;
import com.petshop.backend.dto.response.vendor.VendorOrderSummaryResponse;
import com.petshop.backend.entity.order.Order;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.ConflictException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.order.OrderRepository;
import com.petshop.backend.security.CurrentUser;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.order.OrderLifecycleService;
import com.petshop.backend.service.order.OrderMapper;
import com.petshop.backend.service.order.OrderSpecifications;
import com.petshop.backend.service.vendor.VendorOrderService;
import com.petshop.backend.util.MoneyUtils;
import com.petshop.backend.util.PageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class VendorOrderServiceImpl implements VendorOrderService {

    private final OrderRepository orderRepository;
    private final OrderLifecycleService lifecycle;
    private final OrderMapper orderMapper;

    public VendorOrderServiceImpl(OrderRepository orderRepository, OrderLifecycleService lifecycle, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.lifecycle = lifecycle;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getVendorOrders(String status, String month, String q, boolean includeArchived,
                                                       int page, int size, UserPrincipal currentUser) {
        CurrentUser.require(currentUser);
        YearMonth ym = parseMonth(month);
        Page<Order> result = orderRepository.findAll(
                OrderSpecifications.filter(null, scope(currentUser), OrderStatus.parse(status), q,
                        ym == null ? null : ym.atDay(1).atStartOfDay(),
                        ym == null ? null : ym.plusMonths(1).atDay(1).atStartOfDay(),
                        includeArchived ? null : Boolean.FALSE),
                PageUtils.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id")));
        return PageResponse.of(result, orderMapper.toResponses(result.getContent()));
    }

    @Override
    @Transactional(readOnly = true)
    public VendorOrderSummaryResponse getSummary(String month, UserPrincipal currentUser) {
        CurrentUser.require(currentUser);
        YearMonth ym = parseMonth(month);
        if (ym == null) ym = YearMonth.now();
        List<Order> orders = orderRepository.findAll(OrderSpecifications.filter(null, scope(currentUser), null, null,
                ym.atDay(1).atStartOfDay(), ym.plusMonths(1).atDay(1).atStartOfDay(), null));

        Map<String, Long> statusCounts = new LinkedHashMap<>();
        for (OrderStatus s : OrderStatus.values()) statusCounts.put(s.name(), 0L);
        BigDecimal revenue = BigDecimal.ZERO;
        long counted = 0;
        for (Order o : orders) {
            statusCounts.merge(o.getStatus().name(), 1L, Long::sum);
            if (o.getStatus() != OrderStatus.CANCELLED) {
                revenue = revenue.add(MoneyUtils.nz(o.getTotalAmount()));
                counted++;
            }
        }
        return VendorOrderSummaryResponse.builder()
                .month(ym.toString())
                .revenue(MoneyUtils.scale(revenue))
                .orderCount(counted)
                .averageOrderValue(counted == 0 ? MoneyUtils.scale(BigDecimal.ZERO)
                        : revenue.divide(BigDecimal.valueOf(counted), 2, RoundingMode.HALF_UP))
                .deliveredCount(statusCounts.get(OrderStatus.DELIVERED.name()))
                .cancelledCount(statusCounts.get(OrderStatus.CANCELLED.name()))
                .statusCounts(statusCounts)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getOrderMonths(UserPrincipal currentUser) {
        CurrentUser.require(currentUser);
        return orderRepository.findOrderMonths(currentUser.getId());
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request, UserPrincipal currentUser) {
        Order order = owned(orderId, currentUser);
        OrderStatus next = OrderStatus.parse(request.getStatus());
        if (next == OrderStatus.PENDING) {
            throw new BadRequestException("INVALID_STATUS_TRANSITION", "An order cannot be moved back to pending");
        }
        if (request.getCourierName() != null && !request.getCourierName().isBlank()) {
            order.setCourierName(request.getCourierName().trim());
        }
        if (request.getEstimatedDeliveryFrom() != null) order.setEstimatedDeliveryFrom(request.getEstimatedDeliveryFrom());
        if (request.getEstimatedDeliveryTo() != null) order.setEstimatedDeliveryTo(request.getEstimatedDeliveryTo());
        String note = request.getNote() != null && !request.getNote().isBlank() ? request.getNote().trim()
                : next == OrderStatus.CANCELLED ? "Cancelled by the store" : null;
        return orderMapper.toResponse(lifecycle.transition(order, next, currentUser.getId(), note));
    }

    /** Hides a closed order from the vendor's list; the order itself is kept. */
    @Override
    @Transactional
    public OrderResponse archiveOrder(Long orderId, UserPrincipal currentUser) {
        Order order = owned(orderId, currentUser);
        if (!OrderStatus.CLOSED.contains(order.getStatus())) {
            throw new ConflictException("ORDER_NOT_CLOSED", "Only delivered or cancelled orders can be archived");
        }
        order.setArchivedByVendor(true);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public int archiveClosedOrders(UserPrincipal currentUser) {
        CurrentUser.require(currentUser);
        List<Order> closed = orderRepository.findByVendorIdAndStatusInAndArchivedByVendorFalse(currentUser.getId(), OrderStatus.CLOSED);
        closed.forEach(o -> o.setArchivedByVendor(true));
        orderRepository.saveAll(closed);
        return closed.size();
    }

    private Order owned(Long orderId, UserPrincipal currentUser) {
        CurrentUser.require(currentUser);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        if (!currentUser.isAdmin() && !Objects.equals(order.getVendorId(), currentUser.getId())) {
            // 404 so other stores' order ids are not disclosed.
            throw new ResourceNotFoundException("Order", "id", orderId);
        }
        return order;
    }

    /** Admins see all orders; vendors only their own. */
    private static Long scope(UserPrincipal currentUser) {
        return currentUser.isAdmin() ? null : currentUser.getId();
    }

    private static YearMonth parseMonth(String month) {
        if (month == null || month.isBlank()) return null;
        try {
            return YearMonth.parse(month.trim());
        } catch (DateTimeParseException e) {
            throw new BadRequestException("INVALID_MONTH", "month must look like 2026-09");
        }
    }
}
