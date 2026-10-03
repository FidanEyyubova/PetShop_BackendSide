package com.petshop.backend.dto.request.vendor;

import com.petshop.backend.common.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VendorBankDetailsRequest {

    @NotBlank(message = "IBAN is required")
    @Pattern(regexp = ValidationPatterns.IBAN, message = ValidationPatterns.IBAN_MESSAGE)
    @Schema(example = "AZ21NABZ00000000137010001944")
    private String iban;

    @NotBlank(message = "Bank name is required")
    @Size(max = 150)
    private String bankName;

    @Size(max = 150)
    @Schema(description = "Defaults to the account's full name")
    private String accountHolder;

    @NotBlank(message = "VÖEN is required")
    @Pattern(regexp = ValidationPatterns.VOEN, message = ValidationPatterns.VOEN_MESSAGE)
    @Schema(example = "1234567890")
    private String voen;
}
