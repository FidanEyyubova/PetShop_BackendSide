package com.petshop.backend.service.order;

import com.petshop.backend.dto.response.order.OrderItemResponse;
import com.petshop.backend.dto.response.order.OrderResponse;
import com.petshop.backend.dto.response.order.OrderStatusHistoryResponse;
import com.petshop.backend.entity.order.Order;
import com.petshop.backend.entity.order.OrderItem;
import com.petshop.backend.entity.order.OrderStatusHistory;
import com.petshop.backend.common.Language;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.i18n.MessageTranslator;
import com.petshop.backend.service.product.ProductMapper;
import com.petshop.backend.repository.order.OrderItemRepository;
import com.petshop.backend.repository.order.OrderStatusHistoryRepository;
import com.petshop.backend.util.MoneyUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private final OrderItemRepository itemRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final ProductRepository productRepository;
    private final MessageTranslator translator;

    public OrderMapper(OrderItemRepository itemRepository, OrderStatusHistoryRepository historyRepository,
                       ProductRepository productRepository, MessageTranslator translator) {
        this.translator = translator;
        this.itemRepository = itemRepository;
        this.historyRepository = historyRepository;
        this.productRepository = productRepository;
    }

    public List<OrderResponse> toResponses(List<Order> orders) {
        if (orders.isEmpty()) return List.of();
        List<Long> ids = orders.stream().map(Order::getId).toList();
        Map<Long, List<OrderItem>> items = itemRepository.findByOrderIdIn(ids).stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));
        Map<Long, List<OrderStatusHistory>> history = historyRepository.findByOrderIdInOrderByCreatedAtAsc(ids).stream()
                .collect(Collectors.groupingBy(OrderStatusHistory::getOrderId));
        Map<Long, String> titles = localizedTitles(items.values().stream().flatMap(List::stream).toList());
        return orders.stream()
                .map(o -> toResponse(o, items.getOrDefault(o.getId(), List.of()), history.getOrDefault(o.getId(), List.of()), titles))
                .toList();
    }

    public OrderResponse toResponse(Order order) {
        return toResponses(List.of(order)).get(0);
    }

    private Map<Long, String> localizedTitles(List<OrderItem> items) {
        if (Language.current() == Language.DEFAULT || items.isEmpty()) return Map.of();
        Set<Long> productIds = items.stream().map(OrderItem::getProductId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> titles = new HashMap<>();
        productRepository.findAllById(productIds).forEach(p -> titles.put(p.getId(), ProductMapper.localizedTitle(p)));
        return titles;
    }

    private OrderResponse toResponse(Order o, List<OrderItem> items, List<OrderStatusHistory> history, Map<Long, String> titles) {
        List<OrderItemResponse> lines = items.stream()
                .sorted(Comparator.comparing(OrderItem::getId))
                .map(i -> OrderItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProductId())
                        .productTitle(titles.getOrDefault(i.getProductId(), i.getProductTitle()))
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
                .cancelReason(translator.translate(o.getCancelReason()))
                .itemCount(items.stream().mapToInt(i -> i.getQuantity() == null ? 0 : i.getQuantity()).sum())
                .items(lines)
                .statusHistory(history.stream()
                        .map(h -> new OrderStatusHistoryResponse(h.getStatus().name(), translator.translate(h.getNote()), h.getCreatedAt()))
                        .toList())
                .cancellable(o.getStatus() != null && o.getStatus().isCancellableByBuyer())
                .archivedByVendor(Boolean.TRUE.equals(o.getArchivedByVendor()))
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }
}
