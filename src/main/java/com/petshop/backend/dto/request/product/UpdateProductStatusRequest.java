package com.petshop.backend.dto.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateProductStatusRequest {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "(?i)active|hidden", message = "Status must be 'active' or 'hidden'")
    @Schema(allowableValues = {"active", "hidden"}, example = "hidden")
    private String status;
}
