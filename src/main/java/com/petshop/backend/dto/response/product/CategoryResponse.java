package com.petshop.backend.dto.response.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    @Schema(description = "Category name", example = "Food & Nutrition")
    private String title;
    private String icon;
    @Schema(example = "food-nutrition")
    private String slug;
    @Schema(description = "PRODUCT by default", example = "PRODUCT")
    private String type;
    @Schema(description = "Active products in this category")
    private Long productCount;
    private List<SubCategoryResponse> subCategories;
    private LocalDateTime createdAt;
}
