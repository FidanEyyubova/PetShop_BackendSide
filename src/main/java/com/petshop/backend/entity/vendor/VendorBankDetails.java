package com.petshop.backend.entity.vendor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_bank_details")
public class VendorBankDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_id", nullable = false, unique = true)
    private Long vendorId;

    private String iban;
    private String bankName;
    private String accountHolder;
    private LocalDateTime updatedAt;

    public VendorBankDetails() {}

    public VendorBankDetails(Long id, Long vendorId, String iban, String bankName, String accountHolder, LocalDateTime updatedAt) {
        this.id = id;
        this.vendorId = vendorId;
        this.iban = iban;
        this.bankName = bankName;
        this.accountHolder = accountHolder;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    @PreUpdate
    protected void onSave() {
        this.updatedAt = LocalDateTime.now();
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