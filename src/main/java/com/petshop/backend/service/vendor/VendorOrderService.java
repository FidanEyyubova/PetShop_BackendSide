package com.petshop.backend.service.vendor;

import com.petshop.backend.dto.request.vendor.UpdateOrderStatusRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.order.OrderResponse;
import com.petshop.backend.security.UserPrincipal;
import org.springframework.data.domain.Pageable;

public interface VendorOrderService {
    PageResponse<OrderResponse> getVendorOrders(String status, Pageable pageable, UserPrincipal currentUser);
    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request, UserPrincipal currentUser);
}