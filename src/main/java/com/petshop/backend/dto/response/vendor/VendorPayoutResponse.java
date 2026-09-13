package com.petshop.backend.dto.response.vendor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VendorPayoutResponse {
    private Long id;
    private Long vendorId;
    private BigDecimal amount;
    private String iban;
    private String bankName;
    private String accountHolder;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public VendorPayoutResponse() {}
    public VendorPayoutResponse(Long id, Long vendorId, BigDecimal amount, String iban, String bankName, String accountHolder, String status, LocalDateTime createdAt, LocalDateTime processedAt) {
        this.id = id;
        this.vendorId = vendorId;
        this.amount = amount;
        this.iban = iban;
        this.bankName = bankName;
        this.accountHolder = accountHolder;
        this.status = status;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getAccountHolder() { return accountHolder; }
    public void setAccountHolder(String accountHolder) { this.accountHolder = accountHolder; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}