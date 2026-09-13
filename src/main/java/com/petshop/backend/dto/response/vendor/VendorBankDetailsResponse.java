package com.petshop.backend.dto.response.vendor;

import java.time.LocalDateTime;

public class VendorBankDetailsResponse {
    private Long id;
    private Long vendorId;
    private String iban;
    private String bankName;
    private String accountHolder;
    private LocalDateTime updatedAt;

    public VendorBankDetailsResponse() {}
    public VendorBankDetailsResponse(Long id, Long vendorId, String iban, String bankName, String accountHolder, LocalDateTime updatedAt) {
        this.id = id;
        this.vendorId = vendorId;
        this.iban = iban;
        this.bankName = bankName;
        this.accountHolder = accountHolder;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getAccountHolder() { return accountHolder; }
    public void setAccountHolder(String accountHolder) { this.accountHolder = accountHolder; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}