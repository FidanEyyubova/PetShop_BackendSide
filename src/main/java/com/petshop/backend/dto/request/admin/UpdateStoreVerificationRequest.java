package com.petshop.backend.dto.request.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Platform-owned store fields. */
@Data
public class UpdateStoreVerificationRequest {

    @NotNull
    private Boolean verified;

    @Size(max = 50)
    @Schema(example = "Official partner")
    private String badgeText;

    @Min(0) @Max(100)
    @Schema(description = "Response rate in percent")
    private Integer responseRate;
}
