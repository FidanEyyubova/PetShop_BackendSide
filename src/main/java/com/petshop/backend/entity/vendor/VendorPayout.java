package com.petshop.backend.entity.vendor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_payouts")
public class VendorPayout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @Column(nullable = false)
    private BigDecimal amount;

    private String iban;
    private String bankName;
    private String accountHolder;

    @Column(nullable = false)
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public VendorPayout() {}

    public VendorPayout(Long id, Long vendorId, BigDecimal amount, String iban, String bankName, String accountHolder, String status, LocalDateTime createdAt, LocalDateTime processedAt) {
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

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "PENDING";
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