package com.petshop.backend.dto.request.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddReviewRequest {
    @NotBlank(message = "Hedef növü boş ola bilməz (PRODUCT, SERVICE, STORE)")
    private String targetType;

    @NotNull(message = "Target ID mütləqdir")
    private Long targetId;

    @NotNull(message = "Reyting mütləqdir")
    @Min(value = 1, message = "Minimum 1 ulduz")
    @Max(value = 5, message = "Maksimum 5 ulduz")
    private Integer rating;

    private String comment;
}
