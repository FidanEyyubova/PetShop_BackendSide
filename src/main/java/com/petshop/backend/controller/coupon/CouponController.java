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
@Tag(name = "Coupons", description = "Coupon validation (preview); the discount is recomputed at checkout")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/apply")
    @Operation(summary = "Preview a coupon", description = "Checks the code against a cart total. Unknown or invalid codes return 400 with code COUPON_*.")
    public ResponseEntity<ApiResponse<CouponResponse>> applyCoupon(@Valid @RequestBody ApplyCouponRequest request) {
        CouponResponse response = couponService.applyCoupon(request);
        return ResponseEntity.ok(ApiResponse.success("Promocode applied successfully", response));
    }
}