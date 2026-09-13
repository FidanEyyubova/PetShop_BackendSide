package com.petshop.backend.service.coupon.impl;

import com.petshop.backend.dto.request.coupon.ApplyCouponRequest;
import com.petshop.backend.dto.response.coupon.CouponResponse;
import com.petshop.backend.entity.coupon.Coupon;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.coupon.CouponRepository;
import com.petshop.backend.service.coupon.CouponService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse applyCoupon(ApplyCouponRequest request) {
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            throw new BadRequestException("Promocode code is required");
        }

        String cleanCode = request.getCode().trim().toUpperCase();
        Coupon coupon = couponRepository.findByCodeIgnoreCase(cleanCode)
                .orElseThrow(() -> new ResourceNotFoundException("Promocode not found: " + cleanCode));

        if (Boolean.FALSE.equals(coupon.getActive())) {
            throw new BadRequestException("This promocode is inactive");
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
            throw new BadRequestException("Promocode is not active yet");
        }
        if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
            throw new BadRequestException("Promocode has expired");
        }

        if (coupon.getUsageLimit() != null && coupon.getUsageCount() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new BadRequestException("Promocode usage limit reached");
        }

        BigDecimal cartTotal = request.getCartTotal() != null ? request.getCartTotal() : BigDecimal.ZERO;
        if (coupon.getMinCartAmount() != null && cartTotal.compareTo(coupon.getMinCartAmount()) < 0) {
            throw new BadRequestException("Minimum cart amount for this promocode is " + coupon.getMinCartAmount() + " AZN");
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
            discountAmount = cartTotal.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (coupon.getMaxDiscountAmount() != null && discountAmount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                discountAmount = coupon.getMaxDiscountAmount();
            }
        } else if ("FIXED".equalsIgnoreCase(coupon.getDiscountType()) || "FIXED_AMOUNT".equalsIgnoreCase(coupon.getDiscountType())) {
            discountAmount = coupon.getDiscountValue();
            if (discountAmount.compareTo(cartTotal) > 0) {
                discountAmount = cartTotal;
            }
        }

        BigDecimal finalTotal = cartTotal.subtract(discountAmount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }

        return CouponResponse.builder()
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .discountAmount(discountAmount)
                .finalTotal(finalTotal)
                .valid(true)
                .message("Promocode applied successfully! Discount: " + discountAmount + " AZN")
                .build();
    }
}