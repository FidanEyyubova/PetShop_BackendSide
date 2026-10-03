package com.petshop.backend.dto.response.vendor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorPayoutResponse {
    private Long id;
    private Long vendorId;
    private BigDecimal amount;
    @Schema(description = "Masked IBAN", example = "AZ21 **** **** 1944")
    private String iban;
    private String bankName;
    private String accountHolder;
    @Schema(description = "PENDING, APPROVED, COMPLETED, REJECTED")
    private String status;
    private String adminNote;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}
