package com.petshop.backend.dto.response.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistToggleResponse {
    private Long productId;
    @Schema(description = "true = now in the wishlist")
    private boolean inWishlist;
    private long totalItems;
}
