package com.petshop.backend.dto.response.product;

import com.petshop.backend.dto.common.ProductIngredients;
import com.petshop.backend.dto.common.ProductUsageGuide;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String title;
    private String slug;
    private String sku;
    private String brand;
    private String petType;

    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    private Long subCategoryId;
    private String subCategoryName;

    private Long storeId;
    private String storeName;
    private String storeSlug;
    private Long vendorId;

    @Schema(description = "List price")
    private BigDecimal price;
    private BigDecimal discountPrice;
    @Schema(description = "Price the customer pays")
    private BigDecimal finalPrice;
    private Integer discountPercent;

    @Schema(description = "Cover image (first of images)")
    private String image;
    @Schema(description = "Ordered gallery")
    private List<String> images;
    private List<String> badges;

    private List<String> sizes;
    private List<String> flavors;

    private String description;
    private List<String> keyBenefits;
    private List<String> guarantees;
    private ProductIngredients ingredients;
    private String nutritionInfo;
    private ProductUsageGuide usageGuide;

    private Integer stockQuantity;
    private Boolean inStock;
    @Schema(description = "true when 0 < stock <= 5 (\"Only N left\")")
    private Boolean lowStock;

    private BigDecimal rating;
    private Integer reviewCount;
    private Integer soldCount;
    private Boolean isBestSeller;
    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Schema(description = "az / en / ru versions of the text fields, for edit forms")
    private Map<String, Map<String, Object>> translations;
}
