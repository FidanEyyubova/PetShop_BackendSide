package com.petshop.backend.controller.order;

import com.petshop.backend.dto.request.cart.AddToCartRequest;
import com.petshop.backend.dto.request.cart.MergeCartRequest;
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
@Tag(name = "Cart", description = "Signed-in user's cart with stock checks, discount prices and shipping totals")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "Get my cart", description = "Lines with prices, totals and shipping. Lines that became unavailable are flagged and excluded from the totals.")
    public ResponseEntity<ApiResponse<CartResponse>> get(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Cart fetched", cartService.getCart(currentUser)));
    }

    @PostMapping("/items")
    @Operation(summary = "Add a product", description = "Adds the product (or increases its quantity). Returns 409 when stock is not enough.")
    public ResponseEntity<ApiResponse<CartResponse>> add(@Valid @RequestBody AddToCartRequest request,
                                                         @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Product added to cart", cartService.addToCart(request, currentUser)));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Change a line's quantity", description = "Sets the quantity of one cart line (1-99, at most the stock).")
    public ResponseEntity<ApiResponse<CartResponse>> update(@PathVariable Long itemId,
                                                            @Valid @RequestBody UpdateCartItemRequest request,
                                                            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Cart updated",
                cartService.updateCartItemQuantity(itemId, request.getQuantity(), currentUser)));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove a line", description = "Removes one cart line.")
    public ResponseEntity<ApiResponse<CartResponse>> remove(@PathVariable Long itemId,
                                                            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Product removed from cart", cartService.removeFromCart(itemId, currentUser)));
    }

    @PostMapping("/merge")
    @Operation(summary = "Merge the guest cart after sign-in",
            description = "Send the localStorage cart once after login. Unavailable products are skipped; quantities are capped at stock.")
    public ResponseEntity<ApiResponse<CartResponse>> merge(@Valid @RequestBody MergeCartRequest request,
                                                           @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Cart merged", cartService.mergeCart(request, currentUser)));
    }

    @DeleteMapping({"", "/clear"})
    @Operation(summary = "Empty my cart", description = "Removes every line.")
    public ResponseEntity<ApiResponse<Void>> clear(@AuthenticationPrincipal UserPrincipal currentUser) {
        cartService.clearCart(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared"));
    }
}
