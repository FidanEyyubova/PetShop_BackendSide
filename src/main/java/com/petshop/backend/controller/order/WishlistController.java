package com.petshop.backend.controller.order;

import com.petshop.backend.dto.request.order.AddToWishlistRequest;
import com.petshop.backend.dto.request.order.UpdateWishlistItemRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.order.WishlistItemResponse;
import com.petshop.backend.dto.response.order.WishlistResponse;
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
@Tag(name = "Wishlist Controller", description = "User wishlist management (add, update quantity, remove item, clear wishlist)")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    @Operation(summary = "Get user wishlist")
    public ResponseEntity<ApiResponse<WishlistResponse>> getUserWishlist(@AuthenticationPrincipal UserPrincipal currentUser) {
        WishlistResponse response = wishlistService.getUserWishlist(currentUser);
        return ResponseEntity.ok(
                ApiResponse.<WishlistResponse>builder()
                        .success(true)
                        .message("Wishlist details fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/items/{itemId}")
    @Operation(summary = "Get wishlist item details by ID")
    public ResponseEntity<ApiResponse<WishlistItemResponse>> getWishlistItemById(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        WishlistItemResponse response = wishlistService.getWishlistItemById(itemId, currentUser);
        return ResponseEntity.ok(
                ApiResponse.<WishlistItemResponse>builder()
                        .success(true)
                        .message("Wishlist item fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/items")
    @Operation(summary = "Add product to wishlist")
    public ResponseEntity<ApiResponse<WishlistResponse>> addToWishlist(
            @Valid @RequestBody AddToWishlistRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        WishlistResponse response = wishlistService.addToWishlist(request.getProductId(), request.getQuantity(), currentUser);
        return ResponseEntity.ok(
                ApiResponse.<WishlistResponse>builder()
                        .success(true)
                        .message("Product added to wishlist successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update wishlist item quantity")
    public ResponseEntity<ApiResponse<WishlistResponse>> updateWishlistItemQuantity(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateWishlistItemRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        WishlistResponse response = wishlistService.updateWishlistItemQuantity(itemId, request.getQuantity(), currentUser);
        return ResponseEntity.ok(
                ApiResponse.<WishlistResponse>builder()
                        .success(true)
                        .message("Wishlist item quantity updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from wishlist")
    public ResponseEntity<ApiResponse<WishlistResponse>> removeFromWishlist(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        WishlistResponse response = wishlistService.removeFromWishlist(itemId, currentUser);
        return ResponseEntity.ok(
                ApiResponse.<WishlistResponse>builder()
                        .success(true)
                        .message("Item removed from wishlist successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear all items from wishlist")
    public ResponseEntity<ApiResponse<Void>> clearWishlist(@AuthenticationPrincipal UserPrincipal currentUser) {
        wishlistService.clearWishlist(currentUser);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Wishlist cleared successfully")
                        .data(null)
                        .build()
        );
    }

    @PostMapping("/toggle/{productId}")
    @Operation(summary = "Toggle product in wishlist (add if missing, remove if present)")
    public ResponseEntity<ApiResponse<Boolean>> toggleWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        boolean inWishlist = wishlistService.toggleWishlist(productId, currentUser);
        return ResponseEntity.ok(
                ApiResponse.<Boolean>builder()
                        .success(true)
                        .message(inWishlist ? "Product added to wishlist" : "Product removed from wishlist")
                        .data(inWishlist)
                        .build()
        );
    }
}