package com.petshop.backend.dto.request.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponRequest {

    @NotBlank(message = "Code is required")
    @Pattern(regexp = "^[A-Za-z0-9_-]{3,32}$", message = "Code: 3-32 letters, digits, - or _")
    @Schema(example = "WELCOME10")
    private String code;

    @NotBlank(message = "Discount type is required")
    @Pattern(regexp = "(?i)PERCENTAGE|FIXED", message = "Discount type must be PERCENTAGE or FIXED")
    @Schema(allowableValues = {"PERCENTAGE", "FIXED"})
    private String discountType;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01", message = "Discount value must be positive")
    private BigDecimal discountValue;

    @DecimalMin("0.00")
    private BigDecimal minCartAmount;

    @DecimalMin("0.00")
    @Schema(description = "Cap for percentage coupons")
    private BigDecimal maxDiscountAmount;

    private LocalDateTime validFrom;
    private LocalDateTime validUntil;

    @Min(1)
    private Integer usageLimit;

    private Boolean active = true;
}
