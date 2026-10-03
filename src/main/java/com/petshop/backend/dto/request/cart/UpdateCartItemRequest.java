package com.petshop.backend.dto.request.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemRequest {

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1 (use DELETE to remove)")
    @Max(value = 99, message = "Quantity must be at most 99")
    @Schema(example = "2")
    private Integer quantity;
}
