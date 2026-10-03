package com.petshop.backend.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Schema(example = "buyer@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(example = "Secret123")
    private String password;

    @Schema(description = "Long-lived refresh token (30 days) when true, 24 hours otherwise", example = "true")
    private Boolean rememberMe = false;
}
