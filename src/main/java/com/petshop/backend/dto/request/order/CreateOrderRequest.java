package com.petshop.backend.dto.request.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderRequest {
    @NotBlank(message = "Çatdırılma ünvanı boş ola bilməz")
    private String shippingAddress;

    private String paymentMethod;
}
