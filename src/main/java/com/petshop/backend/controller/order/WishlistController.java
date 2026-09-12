package com.petshop.backend.controller.order;

import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.product.ProductResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.order.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getWishlist(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<ProductResponse> response = wishlistService.getUserWishlist(currentUser);
        return ResponseEntity.ok(
                ApiResponse.<List<ProductResponse>>builder()
                        .success(true)
                        .message("Ä°stÉ™klÉ™r siyahÄ±sÄ±")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> toggleWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        boolean added = wishlistService.toggleWishlist(productId, currentUser);
        String message = added ? "MÉ™hsul istÉ™klÉ™rÉ™ É™lavÉ™ olundu" : "MÉ™hsul istÉ™klÉ™rdÉ™n Ã§Ä±xarÄ±ldÄ±";
        return ResponseEntity.ok(
                ApiResponse.<Boolean>builder()
                        .success(true)
                        .message(message)
                        .data(added)
                        .build()
        );
    }
    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }
}