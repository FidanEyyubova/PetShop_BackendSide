package com.petshop.backend.service.coupon;

import com.petshop.backend.dto.request.coupon.ApplyCouponRequest;
import com.petshop.backend.dto.response.coupon.CouponResponse;

public interface CouponService {
    CouponResponse applyCoupon(ApplyCouponRequest request);
}