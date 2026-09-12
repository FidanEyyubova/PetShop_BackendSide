package com.petshop.backend.dto.request.product;

import jakarta.validation.constraints.NotBlank;

public class UpdateProductStatusRequest {
    @NotBlank(message = "Status boÅŸ ola bilmÉ™z (active/hidden)")
    private String status;

    public UpdateProductStatusRequest() {
    }

    public UpdateProductStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}