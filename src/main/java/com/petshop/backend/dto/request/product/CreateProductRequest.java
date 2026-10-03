package com.petshop.backend.dto.request.product;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.petshop.backend.common.ValidationPatterns;
import com.petshop.backend.dto.common.ProductIngredients;
import com.petshop.backend.dto.common.ProductUsageGuide;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Create or fully replace a product. Vendors always write into their own store. */
@Data
public class CreateProductRequest {

    @NotBlank(message = "Product title is required")
    @Size(max = 200)
    @JsonAlias("name")
    @Schema(description = "Also accepted as `name`", example = "Royal Canin Mini Adult 2 kg")
    private String title;

    @Size(max = 100)
    @Schema(description = "Unique within the store; generated when empty", example = "RC-MINI-2KG")
    private String sku;

    @NotBlank(message = "Brand is required")
    @Size(max = 100)
    @Schema(example = "Royal Canin")
    private String brand;

    @JsonAlias("animalType")
    @Schema(description = "dogs, cats, birds, fish, small-pets, reptiles (singular forms and `rodent` accepted)", example = "dogs")
    private String petType;

    @Schema(description = "Category id; or send categorySlug instead")
    private Long categoryId;

    @Schema(description = "Category slug, e.g. food-nutrition")
    private String categorySlug;

    private Long subCategoryId;

    @Schema(description = "Admin only: target store. Vendors always use their own store.")
    private Long storeId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 2 decimals")
    @Schema(example = "45.90")
    private BigDecimal price;

    @DecimalMin(value = "0.00", message = "Discount price cannot be negative")
    @Digits(integer = 8, fraction = 2)
    @Schema(description = "Must be lower than price", example = "39.90")
    private BigDecimal discountPrice;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    @Max(value = 1_000_000)
    @Schema(example = "25")
    private Integer stockQuantity;

    @JsonAlias("gallery")
    @Size(max = 10, message = "At most 10 images")
    @Schema(description = "Ordered image URLs; the first one is the cover. Also accepted as `gallery`")
    private List<@Pattern(regexp = ValidationPatterns.IMAGE_URL, message = ValidationPatterns.IMAGE_URL_MESSAGE) String> images = new ArrayList<>();

    @Size(max = 10)
    @Schema(example = "[\"New\", \"Vet recommended\"]")
    private List<@Size(max = 50) String> badges = new ArrayList<>();

    @Size(max = 20)
    @Schema(example = "[\"2 kg\", \"4 kg\"]")
    private List<@Size(max = 50) String> sizes = new ArrayList<>();

    @Size(max = 20)
    @Schema(example = "[\"Chicken\", \"Salmon\"]")
    private List<@Size(max = 50) String> flavors = new ArrayList<>();

    @Size(max = 10000)
    private String description;

    @Size(max = 20)
    private List<@Size(max = 200) String> keyBenefits = new ArrayList<>();

    @Size(max = 20)
    private List<@Size(max = 200) String> guarantees = new ArrayList<>();

    @Valid
    private ProductIngredients ingredients;

    @Size(max = 5000)
    @Schema(description = "Legacy free-text nutrition info")
    private String nutritionInfo;

    @Valid
    private ProductUsageGuide usageGuide;

    @Schema(description = "false = hidden from the storefront; default true")
    private Boolean isActive;
}
