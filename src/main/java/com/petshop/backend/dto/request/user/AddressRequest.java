package com.petshop.backend.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Buyer address. title, city, district and street are required (as in the frontend form).
 * fullAddress is optional and is built from the parts when missing.
 */
@Data
public class AddressRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 100)
    @Schema(example = "Ev")
    private String title;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    @Schema(example = "Bakı")
    private String city;

    @NotBlank(message = "District is required")
    @Size(max = 100)
    @Schema(example = "Nəsimi")
    private String district;

    @NotBlank(message = "Street is required")
    @Size(max = 255)
    @Schema(example = "Nizami küç. 10")
    private String street;

    @Size(max = 100)
    @Schema(example = "5-ci mərtəbə, mənzil 21")
    private String floor;

    @Size(max = 500)
    private String note;

    @Size(max = 500)
    private String fullAddress;

    private Boolean isDefault;
}
