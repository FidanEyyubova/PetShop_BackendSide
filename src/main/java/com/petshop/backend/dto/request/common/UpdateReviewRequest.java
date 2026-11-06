package com.petshop.backend.dto.request.common;

import com.petshop.backend.common.ValidationPatterns;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateReviewRequest {

    @Min(value = 1, message = "Rating must be 1-5")
    @Max(value = 5, message = "Rating must be 1-5")
    private Integer rating;

    @Size(min = 2, max = 2000, message = "Comment must be 2-2000 characters")
    private String comment;

    @Size(max = 100)
    private String petName;

    @Pattern(regexp = ValidationPatterns.IMAGE_URL, message = ValidationPatterns.IMAGE_URL_MESSAGE)
    @Size(max = 1000)
    private String petPhoto;
}
