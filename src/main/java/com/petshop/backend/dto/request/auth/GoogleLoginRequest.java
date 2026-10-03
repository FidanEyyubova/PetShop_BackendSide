package com.petshop.backend.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleLoginRequest {

    @NotBlank(message = "Google ID token is required")
    @JsonAlias("credential")
    @Schema(description = "The `credential` (ID token) returned by the Google Sign-In button")
    private String idToken;

    @Schema(description = "Long-lived refresh token (30 days) when true")
    private Boolean rememberMe = false;
}
