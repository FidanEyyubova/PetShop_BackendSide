package com.petshop.backend.dto.request.coupon;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ApplyCouponRequest {

    @NotBlank(message = "Promocode code cannot be blank")
    private String code;

    @NotNull(message = "Cart total is required")
    @Min(value = 0, message = "Cart total cannot be negative")
    private BigDecimal cartTotal;

    public ApplyCouponRequest() {}

    public ApplyCouponRequest(String code, BigDecimal cartTotal) {
        this.code = code;
        this.cartTotal = cartTotal;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public BigDecimal getCartTotal() { return cartTotal; }
    public void setCartTotal(BigDecimal cartTotal) { this.cartTotal = cartTotal; }
}