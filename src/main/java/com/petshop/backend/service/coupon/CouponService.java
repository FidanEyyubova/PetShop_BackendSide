package com.petshop.backend.service.coupon;

import com.petshop.backend.dto.request.coupon.ApplyCouponRequest;
import com.petshop.backend.dto.request.coupon.CouponRequest;
import com.petshop.backend.dto.response.coupon.CouponAdminResponse;
import com.petshop.backend.dto.response.coupon.CouponResponse;
import com.petshop.backend.entity.coupon.Coupon;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {

    CouponResponse applyCoupon(ApplyCouponRequest request);

    Applied validate(String code, BigDecimal subtotal);

    void incrementUsage(Long couponId);

    List<CouponAdminResponse> getAll();

    CouponAdminResponse create(CouponRequest request);

    CouponAdminResponse update(Long id, CouponRequest request);

    void delete(Long id);

    record Applied(Coupon coupon, BigDecimal discount) {}
}
