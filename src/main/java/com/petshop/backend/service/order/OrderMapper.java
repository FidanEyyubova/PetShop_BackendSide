package com.petshop.backend.service.order;

import com.petshop.backend.dto.response.order.OrderItemResponse;
import com.petshop.backend.dto.response.order.OrderResponse;
import com.petshop.backend.dto.response.order.OrderStatusHistoryResponse;
import com.petshop.backend.entity.order.Order;
import com.petshop.backend.entity.order.OrderItem;
import com.petshop.backend.entity.order.OrderStatusHistory;
import com.petshop.backend.repository.order.OrderItemRepository;
import com.petshop.backend.repository.order.OrderStatusHistoryRepository;
import com.petshop.backend.util.MoneyUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/** Maps orders with their lines and tracking history; loads both in one query per batch. */
@Component
public class OrderMapper {

    private final OrderItemRepository itemRepository;
    private final OrderStatusHistoryRepository historyRepository;

    public OrderMapper(OrderItemRepository itemRepository, OrderStatusHistoryRepository historyRepository) {
        this.itemRepository = itemRepository;
        this.historyRepository = historyRepository;
    }

    public List<OrderResponse> toResponses(List<Order> orders) {
        if (orders.isEmpty()) return List.of();
        List<Long> ids = orders.stream().map(Order::getId).toList();
        Map<Long, List<OrderItem>> items = itemRepository.findByOrderIdIn(ids).stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));
        Map<Long, List<OrderStatusHistory>> history = historyRepository.findByOrderIdInOrderByCreatedAtAsc(ids).stream()
                .collect(Collectors.groupingBy(OrderStatusHistory::getOrderId));
        return orders.stream()
                .map(o -> toResponse(o, items.getOrDefault(o.getId(), List.of()), history.getOrDefault(o.getId(), List.of())))
                .toList();
    }

    public OrderResponse toResponse(Order order) {
        return toResponses(List.of(order)).get(0);
    }

    public OrderResponse toResponse(Order o, List<OrderItem> items, List<OrderStatusHistory> history) {
        List<OrderItemResponse> lines = items.stream()
                .sorted(Comparator.comparing(OrderItem::getId))
                .map(i -> OrderItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProductId())
                        .productTitle(i.getProductTitle())
                        .productImage(i.getProductImage())
                        .unitPrice(MoneyUtils.scale(i.getUnitPrice()))
                        .originalPrice(MoneyUtils.scale(i.getOriginalPrice() != null ? i.getOriginalPrice() : i.getUnitPrice()))
                        .quantity(i.getQuantity())
                        .subtotal(MoneyUtils.scale(i.getSubtotal()))
                        .build())
                .toList();
        return OrderResponse.builder()
                .id(o.getId())
                .orderNumber(o.getOrderNumber())
                .checkoutGroup(o.getCheckoutGroup())
                .buyerId(o.getBuyerId())
                .customerName(o.getCustomerName())
                .customerPhone(o.getCustomerPhone())
                .storeId(o.getStoreId())
                .storeName(o.getStoreName())
                .vendorId(o.getVendorId())
                .status(o.getStatus() == null ? null : o.getStatus().name())
                .paymentStatus(o.getPaymentStatus() == null ? null : o.getPaymentStatus().name())
                .paymentMethod(o.getPaymentMethod() == null ? null : o.getPaymentMethod().name())
                .subtotal(MoneyUtils.scale(o.getSubtotal() != null ? o.getSubtotal() : o.getTotalAmount()))
                .shippingFee(MoneyUtils.scale(o.getShippingFee()))
                .discountAmount(MoneyUtils.scale(o.getDiscountAmount()))
                .couponCode(o.getCouponCode())
                .totalAmount(MoneyUtils.scale(o.getTotalAmount()))
                .shippingAddress(o.getShippingAddress())
                .note(o.getNote())
                .estimatedDeliveryFrom(o.getEstimatedDeliveryFrom())
                .estimatedDeliveryTo(o.getEstimatedDeliveryTo())
                .deliveredAt(o.getDeliveredAt())
                .courierName(o.getCourierName())
                .cancelReason(o.getCancelReason())
                .itemCount(items.stream().mapToInt(i -> i.getQuantity() == null ? 0 : i.getQuantity()).sum())
                .items(lines)
                .statusHistory(history.stream()
                        .map(h -> new OrderStatusHistoryResponse(h.getStatus().name(), h.getNote(), h.getCreatedAt()))
                        .toList())
                .cancellable(o.getStatus() != null && o.getStatus().isCancellableByBuyer())
                .archivedByVendor(Boolean.TRUE.equals(o.getArchivedByVendor()))
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }
}
