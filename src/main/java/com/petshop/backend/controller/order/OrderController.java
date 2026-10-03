package com.petshop.backend.controller.order;

import com.petshop.backend.dto.request.order.CancelOrderRequest;
import com.petshop.backend.dto.request.order.CreateOrderRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.order.CheckoutResponse;
import com.petshop.backend.dto.response.order.OrderResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Checkout, order history, tracking and cancellation for buyers")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Place an order (checkout)", description = "Orders the cart (or the given items). Stock is reserved and prices come from the server. Items from several stores create one order per store.")
    public ResponseEntity<ApiResponse<CheckoutResponse>> create(@Valid @RequestBody CreateOrderRequest request,
                                                                @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed", orderService.createOrder(request, currentUser)));
    }

    @GetMapping
    @Operation(summary = "List my orders", description = "Newest first; filter by status and search order number, store or item title.")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> list(
            @Parameter(description = "pending, processing, shipping, delivered, cancelled") @RequestParam(required = false) String status,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Orders fetched", orderService.getUserOrders(status, q, page, size, currentUser)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Order details with tracking timeline", description = "Visible to the buyer, the store's vendor and admins.")
    public ResponseEntity<ApiResponse<OrderResponse>> get(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Order fetched", orderService.getOrderById(id, currentUser)));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Order details by order number (ORD-...)")
    public ResponseEntity<ApiResponse<OrderResponse>> getByNumber(@PathVariable String orderNumber,
                                                                  @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Order fetched", orderService.getOrderByNumber(orderNumber, currentUser)));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel my order", description = "Allowed while pending or processing; stock is returned.")
    public ResponseEntity<ApiResponse<OrderResponse>> cancel(@PathVariable Long id,
                                                             @RequestBody(required = false) CancelOrderRequest request,
                                                             @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Order cancelled", orderService.cancelOrder(id, request, currentUser)));
    }
}
