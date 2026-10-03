package com.petshop.backend.dto.request.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToWishlistRequest {

    @NotNull(message = "Product id is required")
    private Long productId;
}
