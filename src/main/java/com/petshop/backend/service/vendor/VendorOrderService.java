package com.petshop.backend.service.vendor;

import com.petshop.backend.dto.request.vendor.UpdateOrderStatusRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.order.OrderResponse;
import com.petshop.backend.dto.response.vendor.VendorOrderSummaryResponse;
import com.petshop.backend.security.UserPrincipal;

import java.util.List;

public interface VendorOrderService {
    PageResponse<OrderResponse> getVendorOrders(String status, String month, String q, boolean includeArchived,
                                                int page, int size, UserPrincipal currentUser);
    VendorOrderSummaryResponse getSummary(String month, UserPrincipal currentUser);
    List<String> getOrderMonths(UserPrincipal currentUser);
    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request, UserPrincipal currentUser);
    OrderResponse archiveOrder(Long orderId, UserPrincipal currentUser);
    int archiveClosedOrders(UserPrincipal currentUser);
}
