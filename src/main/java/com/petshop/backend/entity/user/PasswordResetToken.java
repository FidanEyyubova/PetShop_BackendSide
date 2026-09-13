package com.petshop.backend.entity.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private Boolean used = false;

    private LocalDateTime createdAt;

    public PasswordResetToken() {}

    public PasswordResetToken(Long id, String email, String code, LocalDateTime expiryDate, Boolean used, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.code = code;
        this.expiryDate = expiryDate;
        this.used = used != null ? used : false;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.used == null) this.used = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public Boolean getUsed() { return used; }
    public void setUsed(Boolean used) { this.used = used; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static PasswordResetTokenBuilder builder() { return new PasswordResetTokenBuilder(); }

    public static class PasswordResetTokenBuilder {
        private Long id;
        private String email;
        private String code;
        private LocalDateTime expiryDate;
        private Boolean used = false;
        private LocalDateTime createdAt;

        public PasswordResetTokenBuilder id(Long id) { this.id = id; return this; }
        public PasswordResetTokenBuilder email(String email) { this.email = email; return this; }
        public PasswordResetTokenBuilder code(String code) { this.code = code; return this; }
        public PasswordResetTokenBuilder expiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; return this; }
        public PasswordResetTokenBuilder used(Boolean used) { this.used = used; return this; }
        public PasswordResetTokenBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public PasswordResetToken build() {
            return new PasswordResetToken(id, email, code, expiryDate, used, createdAt);
        }
    }
}