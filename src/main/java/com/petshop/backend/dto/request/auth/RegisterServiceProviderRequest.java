package com.petshop.backend.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.petshop.backend.common.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterServiceProviderRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100)
    private String fullName;

    @NotBlank(message = "Center name is required")
    @Size(min = 2, max = 150)
    @Schema(example = "PawSpa Grooming")
    private String centerName;

    @NotBlank(message = "Service type is required")
    @Schema(description = "grooming, veterinary or training", example = "grooming")
    private String serviceType;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Phone is required")
    @JsonAlias("phoneNum")
    @Pattern(regexp = ValidationPatterns.PHONE, message = ValidationPatterns.PHONE_MESSAGE)
    private String phone;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = ValidationPatterns.PASSWORD, message = ValidationPatterns.PASSWORD_MESSAGE)
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    private Boolean agreeTerms;

    @Size(max = 100)
    private String city;

    @Size(max = 300)
    private String address;

    @Pattern(regexp = ValidationPatterns.IMAGE_URL, message = ValidationPatterns.IMAGE_URL_MESSAGE)
    @Size(max = 1000)
    private String logoUrl;
}
