package com.petshop.backend.dto.request.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MergeWishlistRequest {

    @NotNull
    @Size(max = 200)
    @Schema(description = "Product ids from the guest (localStorage) wishlist", example = "[1, 2, 3]")
    private List<@NotNull Long> productIds = new ArrayList<>();
}
