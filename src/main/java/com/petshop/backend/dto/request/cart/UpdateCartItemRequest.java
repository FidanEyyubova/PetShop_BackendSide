package com.petshop.backend.dto.request.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemRequest {
    @NotNull(message = "Miqdar mütləqdir")
    @Min(value = 1, message = "Miqdar minimum 1 olmalıdır")
    private Integer quantity;
}
