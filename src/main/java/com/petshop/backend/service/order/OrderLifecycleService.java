package com.petshop.backend.service.order;

import com.petshop.backend.common.OrderStatus;
import com.petshop.backend.common.PaymentMethod;
import com.petshop.backend.common.PaymentStatus;
import com.petshop.backend.entity.order.Order;
import com.petshop.backend.entity.order.OrderItem;
import com.petshop.backend.entity.order.OrderStatusHistory;
import com.petshop.backend.entity.product.Product;
import com.petshop.backend.exception.ConflictException;
import com.petshop.backend.repository.order.OrderItemRepository;
import com.petshop.backend.repository.order.OrderRepository;
import com.petshop.backend.repository.order.OrderStatusHistoryRepository;
import com.petshop.backend.repository.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Single place for status transitions and their side effects (stock, payment status, history). */
@Service
public class OrderLifecycleService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository itemRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final ProductRepository productRepository;

    public OrderLifecycleService(OrderRepository orderRepository, OrderItemRepository itemRepository,
                                 OrderStatusHistoryRepository historyRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.historyRepository = historyRepository;
        this.productRepository = productRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Order transition(Order order, OrderStatus next, Long actorId, String note) {
        OrderStatus current = order.getStatus();
        if (current == next) {
            throw new ConflictException("STATUS_UNCHANGED", "Order is already " + next.name().toLowerCase());
        }
        if (!current.canTransitionTo(next)) {
            throw new ConflictException("INVALID_STATUS_TRANSITION",
                    "Cannot change order status from " + current.name().toLowerCase() + " to " + next.name().toLowerCase());
        }
        order.setStatus(next);
        if (next == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
            if (order.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY) {
                order.setPaymentStatus(PaymentStatus.PAID);
            }
        }
        if (next == OrderStatus.CANCELLED) {
            order.setCancelReason(note);
            restoreStock(order);
            if (order.getPaymentStatus() == PaymentStatus.PAID) {
                order.setPaymentStatus(PaymentStatus.REFUNDED);
            }
        }
        Order saved = orderRepository.save(order);
        record(saved.getId(), next, actorId, note);
        return saved;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void record(Long orderId, OrderStatus status, Long actorId, String note) {
        historyRepository.save(OrderStatusHistory.builder()
                .orderId(orderId)
                .status(status)
                .changedBy(actorId)
                .note(note)
                .build());
    }

    /** Puts the units of a cancelled order back in stock and removes them from the sales counter. */
    private void restoreStock(Order order) {
        List<OrderItem> items = itemRepository.findByOrderId(order.getId());
        Map<Long, Product> products = productRepository.findAllByIdForUpdate(
                        items.stream().map(OrderItem::getProductId).distinct().toList())
                .stream().collect(Collectors.toMap(Product::getId, Function.identity()));
        for (OrderItem item : items) {
            Product p = products.get(item.getProductId());
            if (p == null) continue;
            p.setStockQuantity((p.getStockQuantity() == null ? 0 : p.getStockQuantity()) + item.getQuantity());
            p.setSoldCount(Math.max(0, (p.getSoldCount() == null ? 0 : p.getSoldCount()) - item.getQuantity()));
        }
        productRepository.saveAll(products.values());
    }
}
