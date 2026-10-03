package com.petshop.backend.dto.response.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Compact product used by grids, search, wishlist, related and best sellers. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCardResponse {
    private Long id;
    private String title;
    private String slug;
    private String brand;
    private String sku;

    @Schema(example = "dogs")
    private String petType;

    private Long categoryId;
    private String categoryName;
    private String categorySlug;

    private Long storeId;
    private String storeName;

    @Schema(description = "List price", example = "45.90")
    private BigDecimal price;
    @Schema(description = "Discounted price or null", example = "39.90")
    private BigDecimal discountPrice;
    @Schema(description = "Price the customer pays", example = "39.90")
    private BigDecimal finalPrice;
    @Schema(description = "Discount percentage (0 when none)", example = "13")
    private Integer discountPercent;

    @Schema(description = "Cover image URL")
    private String image;
    private List<String> badges;

    private BigDecimal rating;
    private Integer reviewCount;

    private Integer stockQuantity;
    private Boolean inStock;
    private Boolean isBestSeller;
    private Integer soldCount;

    @Schema(description = "false = hidden by the vendor (only visible in vendor/admin lists)")
    private Boolean isActive;

    private LocalDateTime createdAt;
}
