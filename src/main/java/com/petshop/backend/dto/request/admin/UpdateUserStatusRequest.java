package com.petshop.backend.dto.request.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserStatusRequest {

    @NotBlank
    @Pattern(regexp = "(?i)ACTIVE|PENDING_APPROVAL|REJECTED|SUSPENDED",
            message = "status must be ACTIVE, PENDING_APPROVAL, REJECTED or SUSPENDED")
    @Schema(description = "ACTIVE approves a seller; SUSPENDED blocks the account and hides a vendor's products",
            example = "ACTIVE")
    private String status;

    @Size(max = 500)
    @Schema(description = "Optional reason, emailed to the user")
    private String reason;
}
