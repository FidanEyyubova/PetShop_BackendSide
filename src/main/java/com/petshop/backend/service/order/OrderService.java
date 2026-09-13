package com.petshop.backend.service.order;

import com.petshop.backend.dto.request.order.CreateOrderRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.order.OrderResponse;
import com.petshop.backend.security.UserPrincipal;
import org.springframework.lang.NonNull;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request, UserPrincipal currentUser);
    PageResponse<OrderResponse> getUserOrders(UserPrincipal currentUser, @NonNull Pageable pageable);
    OrderResponse getOrderById(Long id, UserPrincipal currentUser);
    OrderResponse cancelOrder(Long id, UserPrincipal currentUser);
}