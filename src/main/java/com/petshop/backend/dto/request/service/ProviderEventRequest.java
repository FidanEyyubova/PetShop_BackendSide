package com.petshop.backend.dto.request.service;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProviderEventRequest {

    @NotBlank
    @Pattern(regexp = "(?i)PAGE_VIEW|CONTACT_CLICK|WISHLIST_ADD", message = "type must be PAGE_VIEW, CONTACT_CLICK or WISHLIST_ADD")
    @Schema(allowableValues = {"PAGE_VIEW", "CONTACT_CLICK", "WISHLIST_ADD"}, example = "PAGE_VIEW")
    private String type;
}
