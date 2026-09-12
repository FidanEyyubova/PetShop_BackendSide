package com.petshop.backend.controller.order;

import com.petshop.backend.dto.request.cart.AddToCartRequest;
import com.petshop.backend.dto.request.cart.UpdateCartItemRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.order.CartResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.order.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@AuthenticationPrincipal UserPrincipal currentUser) {
        CartResponse response = cartService.getCart(currentUser);
        return ResponseEntity.ok(
                ApiResponse.<CartResponse>builder()
                        .success(true)
                        .message("SÉ™bÉ™t mÉ™lumatlarÄ±")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        CartResponse response = cartService.addToCart(request, currentUser);
        return ResponseEntity.ok(
                ApiResponse.<CartResponse>builder()
                        .success(true)
                        .message("MÉ™hsul sÉ™bÉ™tÉ™ É™lavÉ™ olundu")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItemQuantity(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        CartResponse response = cartService.updateCartItemQuantity(itemId, request.getQuantity(), currentUser);
        return ResponseEntity.ok(
                ApiResponse.<CartResponse>builder()
                        .success(true)
                        .message("MÉ™hsul sayÄ± yenilÉ™ndi")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeFromCart(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        CartResponse response = cartService.removeFromCart(itemId, currentUser);
        return ResponseEntity.ok(
                ApiResponse.<CartResponse>builder()
                        .success(true)
                        .message("MÉ™hsul sÉ™bÉ™tdÉ™n silindi")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal UserPrincipal currentUser) {
        cartService.clearCart(currentUser);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("SÉ™bÉ™t tÉ™mizlÉ™ndi")
                        .data(null)
                        .build()
        );
    }
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
}