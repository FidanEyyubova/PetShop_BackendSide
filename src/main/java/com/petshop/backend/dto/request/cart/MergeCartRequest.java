package com.petshop.backend.dto.request.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MergeCartRequest {

    @NotNull
    @Size(max = 100)
    @Valid
    @Schema(description = "Items from the guest cart; quantities are added to the server cart and capped at stock")
    private List<AddToCartRequest> items = new ArrayList<>();
}
