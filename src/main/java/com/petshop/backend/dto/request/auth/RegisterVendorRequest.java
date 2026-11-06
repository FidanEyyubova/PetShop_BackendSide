package com.petshop.backend.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.petshop.backend.common.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterVendorRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100)
    @Schema(example = "Rauf Əliyev")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 150)
    @Schema(example = "vendor@example.com")
    private String email;

    @NotBlank(message = "Phone is required")
    @JsonAlias("phoneNum")
    @Pattern(regexp = ValidationPatterns.PHONE, message = ValidationPatterns.PHONE_MESSAGE)
    @Schema(example = "+994 50 123 45 67")
    private String phone;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = ValidationPatterns.PASSWORD, message = ValidationPatterns.PASSWORD_MESSAGE)
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    private Boolean agreeTerms;

    @NotBlank(message = "Store name is required")
    @Size(min = 2, max = 150)
    @JsonAlias("centerName")
    @Schema(description = "Also accepted as `centerName` (the frontend field name)", example = "PetLand")
    private String storeName;

    @Size(max = 300)
    @Schema(example = "Nizami küç. 10, Bakı")
    private String storeAddress;

    @Size(max = 100)
    @Schema(example = "Bakı")
    private String city;

    @Pattern(regexp = ValidationPatterns.VOEN, message = ValidationPatterns.VOEN_MESSAGE)
    @Schema(example = "1234567890")
    private String voen;

    @JsonAlias("iban")
    @Pattern(regexp = ValidationPatterns.IBAN, message = ValidationPatterns.IBAN_MESSAGE)
    @Schema(example = "AZ21NABZ00000000137010001944")
    private String bankIban;

    @Size(max = 150)
    private String bankName;

    @Pattern(regexp = ValidationPatterns.IMAGE_URL, message = ValidationPatterns.IMAGE_URL_MESSAGE)
    @Size(max = 1000)
    private String logoUrl;
}
