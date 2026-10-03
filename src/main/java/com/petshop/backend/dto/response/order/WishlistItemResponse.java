package com.petshop.backend.dto.response.order;

import com.petshop.backend.dto.response.product.ProductCardResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemResponse {
    private Long id;
    private Long productId;
    private LocalDateTime addedAt;
    private ProductCardResponse product;
}
