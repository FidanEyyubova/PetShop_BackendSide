package com.petshop.backend.dto.request.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePayoutStatusRequest {

    @NotBlank
    @Schema(description = "APPROVED, COMPLETED or REJECTED", example = "APPROVED")
    private String status;

    @Size(max = 500)
    private String note;
}
