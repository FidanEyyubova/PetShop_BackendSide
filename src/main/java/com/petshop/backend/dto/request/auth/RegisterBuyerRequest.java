package com.petshop.backend.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.petshop.backend.common.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterBuyerRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be 2-100 characters")
    @Schema(example = "Aysel Məmmədova")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 150)
    @Schema(example = "aysel@example.com")
    private String email;

    @JsonAlias("phoneNum")
    @Pattern(regexp = ValidationPatterns.PHONE, message = ValidationPatterns.PHONE_MESSAGE)
    @Schema(description = "Also accepted as `phoneNum`", example = "+994 50 123 45 67")
    private String phone;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = ValidationPatterns.PASSWORD, message = ValidationPatterns.PASSWORD_MESSAGE)
    @Schema(example = "Secret123")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    @Schema(example = "Secret123")
    private String confirmPassword;

    @Schema(description = "Must be true when sent", example = "true")
    private Boolean agreeTerms;

    @Pattern(regexp = ValidationPatterns.IMAGE_URL, message = ValidationPatterns.IMAGE_URL_MESSAGE)
    @Size(max = 1000)
    @Schema(description = "URL returned by POST /api/v1/files")
    private String avatarUrl;

    @Size(max = 255)
    private String cityAddress;
}
