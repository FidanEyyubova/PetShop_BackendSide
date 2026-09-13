package com.petshop.backend.service.vendor.impl;

import com.petshop.backend.dto.request.vendor.UpdateOrderStatusRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.order.OrderItemResponse;
import com.petshop.backend.dto.response.order.OrderResponse;
import com.petshop.backend.entity.order.Order;
import com.petshop.backend.entity.order.OrderItem;
import com.petshop.backend.entity.product.Product;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.order.OrderItemRepository;
import com.petshop.backend.repository.order.OrderRepository;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.repository.product.StoreRepository;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.vendor.VendorOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VendorOrderServiceImpl implements VendorOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    public VendorOrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            StoreRepository storeRepository,
            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getVendorOrders(String status, Pageable pageable, UserPrincipal currentUser) {
        Long vendorId = currentUser.getId();

        List<Product> vendorProducts = productRepository.findByVendorId(vendorId);
        if (vendorProducts.isEmpty()) {
            return new PageResponse<>(Collections.emptyList(), pageable.getPageNumber(), pageable.getPageSize(), 0, 0, true);
        }

        Set<Long> vendorProductIds = vendorProducts.stream().map(Product::getId).collect(Collectors.toSet());

        List<OrderItem> orderItems = orderItemRepository.findAll().stream()
                .filter(item -> vendorProductIds.contains(item.getProductId()))
                .collect(Collectors.toList());

        Set<Long> vendorOrderIds = orderItems.stream().map(OrderItem::getOrderId).collect(Collectors.toSet());

        if (vendorOrderIds.isEmpty()) {
            return new PageResponse<>(Collections.emptyList(), pageable.getPageNumber(), pageable.getPageSize(), 0, 0, true);
        }

        Page<Order> orderPage = orderRepository.findAll(pageable);

        List<OrderResponse> content = orderPage.getContent().stream()
                .filter(o -> vendorOrderIds.contains(o.getId()))
                .filter(o -> status == null || status.trim().isEmpty() || o.getStatus().equalsIgnoreCase(status))
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(content, orderPage.getNumber(), orderPage.getSize(), orderPage.getTotalElements(), orderPage.getTotalPages(), orderPage.isLast());
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request, UserPrincipal currentUser) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setStatus(request.getStatus().toUpperCase());
        Order updatedOrder = orderRepository.save(order);
        return mapToOrderResponse(updatedOrder);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemResponse> itemResponses = items.stream()
                .map(i -> new OrderItemResponse(i.getId(), i.getProductId(), "Product #" + i.getProductId(), null, i.getUnitPrice(), i.getQuantity(), i.getSubtotal()))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getBuyerId(),
                order.getOrderNumber(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getShippingAddress(),
                order.getCreatedAt(),
                itemResponses
        );
    }
}