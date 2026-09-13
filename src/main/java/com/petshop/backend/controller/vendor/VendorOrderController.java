package com.petshop.backend.controller.vendor;

import com.petshop.backend.dto.request.vendor.UpdateOrderStatusRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.order.OrderResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.vendor.VendorOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Vendor Order Controller", description = "Vendor store incoming orders, order status updates, and order management")
@PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
public class VendorOrderController {

    private final VendorOrderService vendorOrderService;

    public VendorOrderController(VendorOrderService vendorOrderService) {
        this.vendorOrderService = vendorOrderService;
    }

    @GetMapping({"/vendor/orders", "/stores/me/orders"})
    @Operation(summary = "Get vendor store incoming orders with pagination and status filter")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getVendorOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<OrderResponse> response = vendorOrderService.getVendorOrders(status, pageable, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Vendor orders fetched successfully", response));
    }

    @PutMapping("/vendor/orders/{id}/status")
    @Operation(summary = "Update order status (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        OrderResponse response = vendorOrderService.updateOrderStatus(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", response));
    }
}