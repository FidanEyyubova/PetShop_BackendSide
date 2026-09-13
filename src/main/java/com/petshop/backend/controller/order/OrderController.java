package com.petshop.backend.controller.order;

import com.petshop.backend.dto.request.order.CreateOrderRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.order.OrderResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order Controller", description = "Order placement (checkout), order cancellation, order history, and status tracking")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create/Place a new order (Checkout)")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        OrderResponse response = orderService.createOrder(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Order placed successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get user's order history with pagination")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<OrderResponse> response = orderService.getUserOrders(currentUser, pageable);
        return ResponseEntity.ok(ApiResponse.success("Orders fetched successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details by ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        OrderResponse response = orderService.getOrderById(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Order details fetched successfully", response));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel an active order by ID")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        OrderResponse response = orderService.cancelOrder(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", response));
    }
}