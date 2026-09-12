package com.petshop.backend.dto.request.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateProductStockRequest {
    @NotNull(message = "Stok sayı mütləqdir")
    @Min(value = 0, message = "Stok mənfi ola bilməz")
    private Integer stockQuantity;
}
