package com.petshop.backend.dto.request.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateProductRequest {

    @NotBlank(message = "Məhsul adı boş ola bilməz")
    private String title;

    private String sku;
    private String brand;
    private String petType; // Birds, Dogs, Cats
    private String categoryName;
    private Long categoryId;
    private Long storeId;

    @NotNull(message = "Qiymət mütləqdir")
    @Min(value = 0, message = "Qiymət mənfi ola bilməz")
    private BigDecimal price;

    private BigDecimal discountPrice;

    @NotNull(message = "Stok sayı mütləqdir")
    @Min(value = 0, message = "Stok mənfi ola bilməz")
    private Integer stockQuantity;

    private String badges; // 100% Natural, Veterinary Approved
    private String sizes; // 4.5 kg Bag, 9 kg Bag, 18 kg Sack
    private String flavors; // Black Oil Sunflower & Nuts...
    private String images; // Şəkil URL-ləri

    // Tab 1: Overview & Benefits
    private String description;
    private String keyBenefits;

    // Tab 2: Ingredients & Nutrition
    private String guarantees;
    private String ingredients;
    private String nutritionInfo;

    // Tab 3: Usage & Dosage
    private String usageGuide;
}
