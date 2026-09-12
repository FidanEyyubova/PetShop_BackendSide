package com.petshop.backend.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProductStatusRequest {
    @NotBlank(message = "Status boş ola bilməz (active/hidden)")
    private String status;
}
