package com.petshop.backend.controller.coupon;

import com.petshop.backend.dto.request.coupon.ApplyCouponRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.coupon.CouponResponse;
import com.petshop.backend.service.coupon.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/coupons")
@Tag(name = "Coupon Controller", description = "Promocode and coupon validation & discount calculation endpoints")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/apply")
    @Operation(summary = "Validate promo code and calculate discount amount")
    public ResponseEntity<ApiResponse<CouponResponse>> applyCoupon(@Valid @RequestBody ApplyCouponRequest request) {
        CouponResponse response = couponService.applyCoupon(request);
        return ResponseEntity.ok(ApiResponse.success("Promocode applied successfully", response));
    }
}