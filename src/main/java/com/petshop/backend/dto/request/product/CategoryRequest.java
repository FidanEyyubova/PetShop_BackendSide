package com.petshop.backend.dto.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 100)
    @Schema(example = "Food & Nutrition")
    private String title;

    @Size(max = 500)
    @Schema(description = "Icon name or image URL")
    private String icon;

    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Slug may contain lower-case letters, digits and dashes")
    @Size(max = 100)
    @Schema(description = "Optional; generated from the title when empty", example = "food-nutrition")
    private String slug;

    @Schema(description = "PRODUCT (default) or SERVICE", example = "PRODUCT")
    private String type;
}
