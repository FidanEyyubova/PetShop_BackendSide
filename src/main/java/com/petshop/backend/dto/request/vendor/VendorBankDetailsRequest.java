package com.petshop.backend.dto.request.vendor;

import jakarta.validation.constraints.NotBlank;

public class VendorBankDetailsRequest {
    @NotBlank(message = "IBAN is required")
    private String iban;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "Account holder name is required")
    private String accountHolder;

    public VendorBankDetailsRequest() {}
    public VendorBankDetailsRequest(String iban, String bankName, String accountHolder) {
        this.iban = iban;
        this.bankName = bankName;
        this.accountHolder = accountHolder;
    }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getAccountHolder() { return accountHolder; }
    public void setAccountHolder(String accountHolder) { this.accountHolder = accountHolder; }
}