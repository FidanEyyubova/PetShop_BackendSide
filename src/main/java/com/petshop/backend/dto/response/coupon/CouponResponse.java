package com.petshop.backend.dto.response.coupon;

import java.math.BigDecimal;

public class CouponResponse {

    private String code;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal discountAmount;
    private BigDecimal finalTotal;
    private Boolean valid;
    private String message;

    public CouponResponse() {}

    public CouponResponse(String code, String discountType, BigDecimal discountValue, BigDecimal discountAmount, BigDecimal finalTotal, Boolean valid, String message) {
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.discountAmount = discountAmount;
        this.finalTotal = finalTotal;
        this.valid = valid;
        this.message = message;
    }

    public static CouponResponseBuilder builder() { return new CouponResponseBuilder(); }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public BigDecimal getFinalTotal() { return finalTotal; }
    public void setFinalTotal(BigDecimal finalTotal) { this.finalTotal = finalTotal; }
    public Boolean getValid() { return valid; }
    public void setValid(Boolean valid) { this.valid = valid; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public static class CouponResponseBuilder {
        private String code;
        private String discountType;
        private BigDecimal discountValue;
        private BigDecimal discountAmount;
        private BigDecimal finalTotal;
        private Boolean valid;
        private String message;

        public CouponResponseBuilder code(String code) { this.code = code; return this; }
        public CouponResponseBuilder discountType(String discountType) { this.discountType = discountType; return this; }
        public CouponResponseBuilder discountValue(BigDecimal discountValue) { this.discountValue = discountValue; return this; }
        public CouponResponseBuilder discountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; return this; }
        public CouponResponseBuilder finalTotal(BigDecimal finalTotal) { this.finalTotal = finalTotal; return this; }
        public CouponResponseBuilder valid(Boolean valid) { this.valid = valid; return this; }
        public CouponResponseBuilder message(String message) { this.message = message; return this; }

        public CouponResponse build() {
            return new CouponResponse(code, discountType, discountValue, discountAmount, finalTotal, valid, message);
        }
    }
}