package com.petshop.backend.controller.vendor;

import com.petshop.backend.common.Roles;
import com.petshop.backend.dto.request.vendor.UpdateOrderStatusRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.order.OrderResponse;
import com.petshop.backend.dto.response.vendor.VendorOrderSummaryResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.vendor.VendorOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/vendor/orders")
@PreAuthorize("hasAnyRole('" + Roles.VENDOR + "','" + Roles.ADMIN + "')")
@Tag(name = "Vendor - Orders", description = "Orders received by the vendor's store: list, monthly summary, status changes, archive")
public class VendorOrderController {

    private final VendorOrderService vendorOrderService;

    public VendorOrderController(VendorOrderService vendorOrderService) {
        this.vendorOrderService = vendorOrderService;
    }

    @GetMapping
    @Operation(summary = "List my orders", description = "Orders of my store, newest first; filter by status, month and search. Archived orders are hidden by default.")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> list(
            @Parameter(description = "new/pending, processing, shipping, delivered, cancelled") @RequestParam(required = false) String status,
            @Parameter(description = "yyyy-MM, e.g. 2026-09") @RequestParam(required = false) String month,
            @Parameter(description = "Order number or customer name") @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "false") boolean includeArchived,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Orders fetched",
                vendorOrderService.getVendorOrders(status, month, q, includeArchived, page, size, currentUser)));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get the monthly summary", description = "Revenue (cancelled excluded), order count, average order value and per-status counts.")
    public ResponseEntity<ApiResponse<VendorOrderSummaryResponse>> summary(
            @Parameter(description = "yyyy-MM; default current month") @RequestParam(required = false) String month,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Summary fetched", vendorOrderService.getSummary(month, currentUser)));
    }

    @GetMapping("/months")
    @Operation(summary = "List order months", description = "Months (yyyy-MM) that have orders, for the month picker.")
    public ResponseEntity<ApiResponse<List<String>>> months(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Months fetched", vendorOrderService.getOrderMonths(currentUser)));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Change an order status", description = "pending -> processing -> shipping -> delivered; cancel from pending or processing (stock returned). Invalid moves return 409.")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(@PathVariable Long id,
                                                                   @Valid @RequestBody UpdateOrderStatusRequest request,
                                                                   @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Order status updated",
                vendorOrderService.updateOrderStatus(id, request, currentUser)));
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archive an order", description = "Hides a delivered or cancelled order from the list; nothing is deleted.")
    public ResponseEntity<ApiResponse<OrderResponse>> archive(@PathVariable Long id,
                                                              @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Order archived", vendorOrderService.archiveOrder(id, currentUser)));
    }

    @PostMapping("/archive-closed")
    @Operation(summary = "Archive all closed orders", description = "Hides every delivered and cancelled order from the list.")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> archiveClosed(@AuthenticationPrincipal UserPrincipal currentUser) {
        int count = vendorOrderService.archiveClosedOrders(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Closed orders archived", Map.of("archived", count)));
    }
}
