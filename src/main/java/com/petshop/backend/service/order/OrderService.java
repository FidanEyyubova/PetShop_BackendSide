package com.petshop.backend.service.order;

import com.petshop.backend.dto.request.order.CancelOrderRequest;
import com.petshop.backend.dto.request.order.CreateOrderRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.order.CheckoutResponse;
import com.petshop.backend.dto.response.order.OrderResponse;
import com.petshop.backend.security.UserPrincipal;

public interface OrderService {
    CheckoutResponse createOrder(CreateOrderRequest request, UserPrincipal currentUser);
    PageResponse<OrderResponse> getUserOrders(String status, String q, int page, int size, UserPrincipal currentUser);
    OrderResponse getOrderById(Long id, UserPrincipal currentUser);
    OrderResponse getOrderByNumber(String orderNumber, UserPrincipal currentUser);
    OrderResponse cancelOrder(Long id, CancelOrderRequest request, UserPrincipal currentUser);
}
