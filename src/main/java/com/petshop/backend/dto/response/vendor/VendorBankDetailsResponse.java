package com.petshop.backend.dto.response.vendor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorBankDetailsResponse {
    private Long id;
    private Long vendorId;
    private String iban;
    private String bankName;
    private String accountHolder;
    private String voen;
    private LocalDateTime updatedAt;
}
