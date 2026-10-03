package com.petshop.backend.controller.order;

import com.petshop.backend.dto.request.order.AddToWishlistRequest;
import com.petshop.backend.dto.request.order.MergeWishlistRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.order.WishlistResponse;
import com.petshop.backend.dto.response.order.WishlistToggleResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.order.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wishlist")
@Tag(name = "Wishlist", description = "Favourite products (one row per product; hidden or deleted products are left out)")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    @Operation(summary = "Get my wishlist", description = "Product cards plus a productIds array.")
    public ResponseEntity<ApiResponse<WishlistResponse>> get(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Wishlist fetched", wishlistService.getUserWishlist(currentUser)));
    }

    @PostMapping("/items")
    @Operation(summary = "Add a product", description = "Adds the product (or increases its quantity). Returns 409 when stock is not enough.")
    public ResponseEntity<ApiResponse<WishlistResponse>> add(@Valid @RequestBody AddToWishlistRequest request,
                                                             @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Added to wishlist",
                wishlistService.addToWishlist(request.getProductId(), currentUser)));
    }

    @PostMapping("/toggle/{productId}")
    @Operation(summary = "Toggle a product", description = "Heart icon: adds the product when missing, removes it when present. Returns the new state and count.")
    public ResponseEntity<ApiResponse<WishlistToggleResponse>> toggle(@PathVariable Long productId,
                                                                      @AuthenticationPrincipal UserPrincipal currentUser) {
        WishlistToggleResponse result = wishlistService.toggleWishlist(productId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(result.isInWishlist() ? "Added to wishlist" : "Removed from wishlist", result));
    }

    @DeleteMapping("/products/{productId}")
    @Operation(summary = "Remove a product", description = "Removes a product by its product id.")
    public ResponseEntity<ApiResponse<WishlistResponse>> removeProduct(@PathVariable Long productId,
                                                                       @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Removed from wishlist", wishlistService.removeProduct(productId, currentUser)));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove a wishlist item", description = "Removes one row by its wishlist id.")
    public ResponseEntity<ApiResponse<WishlistResponse>> removeItem(@PathVariable Long itemId,
                                                                    @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Removed from wishlist", wishlistService.removeItem(itemId, currentUser)));
    }

    @PostMapping("/merge")
    @Operation(summary = "Merge the guest wishlist after sign-in", description = "Send the localStorage product ids once after login.")
    public ResponseEntity<ApiResponse<WishlistResponse>> merge(@Valid @RequestBody MergeWishlistRequest request,
                                                               @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Wishlist merged", wishlistService.merge(request, currentUser)));
    }

    @DeleteMapping({"", "/clear"})
    @Operation(summary = "Clear my wishlist", description = "Removes every product.")
    public ResponseEntity<ApiResponse<Void>> clear(@AuthenticationPrincipal UserPrincipal currentUser) {
        wishlistService.clearWishlist(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Wishlist cleared"));
    }
}
