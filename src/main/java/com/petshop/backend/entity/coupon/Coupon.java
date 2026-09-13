package com.petshop.backend.entity.coupon;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String discountType;

    @Column(nullable = false)
    private BigDecimal discountValue;

    private BigDecimal minCartAmount;
    private BigDecimal maxDiscountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Integer usageLimit;
    private Integer usageCount;

    @Column(name = "is_active")
    private Boolean active = true;

    private LocalDateTime createdAt;

    public Coupon() {}

    public Coupon(Long id, String code, String discountType, BigDecimal discountValue, BigDecimal minCartAmount, BigDecimal maxDiscountAmount, LocalDateTime validFrom, LocalDateTime validUntil, Integer usageLimit, Integer usageCount, Boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minCartAmount = minCartAmount;
        this.maxDiscountAmount = maxDiscountAmount;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.usageLimit = usageLimit;
        this.usageCount = usageCount;
        this.active = active != null ? active : true;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.active == null) this.active = true;
        if (this.usageCount == null) this.usageCount = 0;
        if (this.discountType == null) this.discountType = "PERCENTAGE";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }

    public BigDecimal getMinCartAmount() { return minCartAmount; }
    public void setMinCartAmount(BigDecimal minCartAmount) { this.minCartAmount = minCartAmount; }

    public BigDecimal getMaxDiscountAmount() { return maxDiscountAmount; }
    public void setMaxDiscountAmount(BigDecimal maxDiscountAmount) { this.maxDiscountAmount = maxDiscountAmount; }

    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }

    public LocalDateTime getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDateTime validUntil) { this.validUntil = validUntil; }

    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }

    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Boolean getIsActive() { return active; }
    public void setIsActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static CouponBuilder builder() { return new CouponBuilder(); }

    public static class CouponBuilder {
        private Long id;
        private String code;
        private String discountType;
        private BigDecimal discountValue;
        private BigDecimal minCartAmount;
        private BigDecimal maxDiscountAmount;
        private LocalDateTime validFrom;
        private LocalDateTime validUntil;
        private Integer usageLimit;
        private Integer usageCount;
        private Boolean active = true;
        private LocalDateTime createdAt;

        public CouponBuilder id(Long id) { this.id = id; return this; }
        public CouponBuilder code(String code) { this.code = code; return this; }
        public CouponBuilder discountType(String discountType) { this.discountType = discountType; return this; }
        public CouponBuilder discountValue(BigDecimal discountValue) { this.discountValue = discountValue; return this; }
        public CouponBuilder minCartAmount(BigDecimal minCartAmount) { this.minCartAmount = minCartAmount; return this; }
        public CouponBuilder maxDiscountAmount(BigDecimal maxDiscountAmount) { this.maxDiscountAmount = maxDiscountAmount; return this; }
        public CouponBuilder validFrom(LocalDateTime validFrom) { this.validFrom = validFrom; return this; }
        public CouponBuilder validUntil(LocalDateTime validUntil) { this.validUntil = validUntil; return this; }
        public CouponBuilder usageLimit(Integer usageLimit) { this.usageLimit = usageLimit; return this; }
        public CouponBuilder usageCount(Integer usageCount) { this.usageCount = usageCount; return this; }
        public CouponBuilder active(Boolean active) { this.active = active; return this; }
        public CouponBuilder isActive(Boolean isActive) { this.active = isActive; return this; }
        public CouponBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Coupon build() {
            return new Coupon(id, code, discountType, discountValue, minCartAmount, maxDiscountAmount, validFrom, validUntil, usageLimit, usageCount, active, createdAt);
        }
    }
}