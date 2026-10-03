package com.petshop.backend.dto.request.common;

import com.petshop.backend.common.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddReviewRequest {

    @NotBlank(message = "Target type is required")
    @Schema(description = "PRODUCT, STORE or SERVICE_PROVIDER (serviceCenter accepted)", example = "PRODUCT")
    private String targetType;

    @NotNull(message = "Target id is required")
    @Schema(description = "Product id, store id or provider user id", example = "12")
    private Long targetId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be 1-5")
    @Max(value = 5, message = "Rating must be 1-5")
    @Schema(example = "5")
    private Integer rating;

    @NotBlank(message = "Comment is required")
    @Size(min = 2, max = 2000, message = "Comment must be 2-2000 characters")
    @Schema(example = "My dog loves it!")
    private String comment;

    @Size(max = 100)
    @Schema(description = "Optional pet name shown with the review", example = "Max")
    private String petName;

    @Pattern(regexp = ValidationPatterns.IMAGE_URL, message = ValidationPatterns.IMAGE_URL_MESSAGE)
    @Size(max = 1000)
    @Schema(description = "Optional pet photo URL (upload via POST /api/v1/files)")
    private String petPhoto;
}
