package com.petshop.backend.dto.response.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponse {
    private List<WishlistItemResponse> items;
    @Schema(description = "Product ids only, same shape as the frontend's localStorage wishlist")
    private List<Long> productIds;
    private Integer totalItems;
}
