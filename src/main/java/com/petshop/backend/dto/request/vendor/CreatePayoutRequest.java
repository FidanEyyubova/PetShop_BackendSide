package com.petshop.backend.dto.request.vendor;

import com.petshop.backend.common.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePayoutRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Amount must be at least 1.00")
    @Digits(integer = 10, fraction = 2)
    @Schema(example = "250.00")
    private BigDecimal amount;

    @Pattern(regexp = ValidationPatterns.IBAN, message = ValidationPatterns.IBAN_MESSAGE)
    private String iban;

    @Size(max = 150)
    private String bankName;

    @Size(max = 150)
    private String accountHolder;
}
