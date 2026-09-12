package com.petshop.backend.dto.request.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateProductStockRequest {
    @NotNull(message = "Stok sayÄ± mÃ¼tlÉ™qdir")
    @Min(value = 0, message = "Stok mÉ™nfi ola bilmÉ™z")
    private Integer stockQuantity;

    public UpdateProductStockRequest() {
    }

    public UpdateProductStockRequest(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
}