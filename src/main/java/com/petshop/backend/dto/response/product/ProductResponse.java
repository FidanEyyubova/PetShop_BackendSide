package com.petshop.backend.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String title;
    private String slug;
    private String sku;
    private String brand;
    private String petType;
    private String categoryName;
    private Long categoryId;
    private Long storeId;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer stockQuantity;
    private String badges;
    private String sizes;
    private String flavors;
    private String images;
    private String description;
    private String keyBenefits;
    private String guarantees;
    private String ingredients;
    private String nutritionInfo;
    private String usageGuide;
    private BigDecimal rating;
}
