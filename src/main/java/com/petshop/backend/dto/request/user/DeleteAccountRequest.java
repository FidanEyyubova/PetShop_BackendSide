package com.petshop.backend.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteAccountRequest {

    @NotBlank(message = "Password is required to delete the account")
    @Schema(description = "Current password, to confirm the deletion")
    private String password;
}
