package com.petshop.backend.dto.request.order;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CancelOrderRequest {

    @Size(max = 500)
    private String reason;
}
