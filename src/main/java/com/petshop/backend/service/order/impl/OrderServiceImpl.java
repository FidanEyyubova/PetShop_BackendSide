package com.petshop.backend.service.order.impl;

import com.petshop.backend.dto.request.order.CreateOrderRequest;
import com.petshop.backend.dto.request.order.OrderItemRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.order.OrderItemResponse;
import com.petshop.backend.dto.response.order.OrderResponse;
import com.petshop.backend.entity.order.Order;
import com.petshop.backend.entity.order.OrderItem;
import com.petshop.backend.entity.product.Product;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.order.OrderItemRepository;
import com.petshop.backend.repository.order.OrderRepository;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.order.CartService;
import com.petshop.backend.service.order.OrderService;
import org.springframework.lang.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, UserPrincipal currentUser) {
        String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> itemsToSave = new ArrayList<>();

        if (request.getItems() != null) {
            for (OrderItemRequest itemReq : request.getItems()) {
                Long prodId = itemReq.getProductId();
                if (prodId == null) {
                    throw new BadRequestException("Məhsul ID-si təyin edilməyib!");
                }
                Product product = productRepository.findById(prodId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product", "id", prodId));

                BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
                totalAmount = totalAmount.add(subtotal);

                itemsToSave.add(OrderItem.builder()
                        .productId(product.getId())
                        .unitPrice(product.getPrice())
                        .quantity(itemReq.getQuantity())
                        .subtotal(subtotal)
                        .build());
            }
        }

        Order order = Order.builder()
                .buyerId(currentUser.getId())
                .orderNumber(orderNumber)
                .totalAmount(totalAmount)
                .status("PENDING")
                .paymentStatus("PAID")
                .shippingAddress(request.getShippingAddress())
                .build();

        Order savedOrder = Objects.requireNonNull(orderRepository.save(order));

        for (OrderItem item : itemsToSave) {
            item.setOrderId(savedOrder.getId());
            orderItemRepository.save(item);
        }

        cartService.clearCart(currentUser);
        return mapToResponse(savedOrder, itemsToSave);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getUserOrders(UserPrincipal currentUser, @NonNull Pageable pageable) {
        Page<Order> page = orderRepository.findByBuyerId(currentUser.getId(), pageable);
        List<OrderResponse> content = page.getContent().stream().map(order -> {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            return mapToResponse(order, items);
        }).toList();

        return PageResponse.<OrderResponse>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id, UserPrincipal currentUser) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return mapToResponse(order, items);
    }

    private OrderResponse mapToResponse(Order order, List<OrderItem> items) {
        List<OrderItemResponse> itemResponses = items.stream().map(item -> {
            Long prodId = item.getProductId();
            Product p = (prodId != null) ? productRepository.findById(prodId).orElse(null) : null;
            return OrderItemResponse.builder()
                    .id(item.getId())
                    .productId(item.getProductId())
                    .productTitle(p != null ? p.getTitle() : "")
                    .productImage(p != null ? p.getImages() : "")
                    .unitPrice(item.getUnitPrice())
                    .quantity(item.getQuantity())
                    .subtotal(item.getSubtotal())
                    .build();
        }).toList();

        return OrderResponse.builder()
                .id(order.getId())
                .buyerId(order.getBuyerId())
                .orderNumber(order.getOrderNumber())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .shippingAddress(order.getShippingAddress())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .build();
    }
    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }
}