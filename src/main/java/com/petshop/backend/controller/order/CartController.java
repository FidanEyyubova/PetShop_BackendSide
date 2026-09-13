package com.petshop.backend.controller.order;

import com.petshop.backend.dto.request.cart.AddToCartRequest;
import com.petshop.backend.dto.request.cart.UpdateCartItemRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.order.CartResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.order.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@Tag(name = "Cart Controller", description = "Shopping cart management")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "Get user shopping cart")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@AuthenticationPrincipal UserPrincipal currentUser) {
        CartResponse response = cartService.getCart(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Cart details fetched successfully", response));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to shopping cart")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        CartResponse response = cartService.addToCart(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Product added to cart successfully", response));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItemQuantity(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        CartResponse response = cartService.updateCartItemQuantity(itemId, request.getQuantity(), currentUser);
        return ResponseEntity.ok(ApiResponse.success("Cart item quantity updated successfully", response));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<ApiResponse<CartResponse>> removeFromCart(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        CartResponse response = cartService.removeFromCart(itemId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart successfully", response));
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear all items from cart")
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal UserPrincipal currentUser) {
        cartService.clearCart(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully"));
    }
}