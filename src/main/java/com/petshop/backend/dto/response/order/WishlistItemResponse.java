package com.petshop.backend.dto.response.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemResponse {
    private Long id;
    private Long productId;
    private String productTitle;
    private String productImage;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer quantity;
    private LocalDateTime createdAt;
}