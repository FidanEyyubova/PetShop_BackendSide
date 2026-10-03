package com.petshop.backend.service.coupon;

import com.petshop.backend.dto.request.coupon.ApplyCouponRequest;
import com.petshop.backend.dto.request.coupon.CouponRequest;
import com.petshop.backend.dto.response.coupon.CouponAdminResponse;
import com.petshop.backend.dto.response.coupon.CouponResponse;
import com.petshop.backend.entity.coupon.Coupon;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {

    /** Validation preview for the cart page (the amount is recomputed at checkout). */
    CouponResponse applyCoupon(ApplyCouponRequest request);

    /** Validates the code against a server-side subtotal; returns the coupon and the discount. */
    Applied validate(String code, BigDecimal subtotal);

    void incrementUsage(Long couponId);

    List<CouponAdminResponse> getAll();

    CouponAdminResponse create(CouponRequest request);

    CouponAdminResponse update(Long id, CouponRequest request);

    void delete(Long id);

    record Applied(Coupon coupon, BigDecimal discount) {}
}
