package com.petshop.backend.service.coupon.impl;

import com.petshop.backend.dto.request.coupon.ApplyCouponRequest;
import com.petshop.backend.dto.request.coupon.CouponRequest;
import com.petshop.backend.dto.response.coupon.CouponAdminResponse;
import com.petshop.backend.dto.response.coupon.CouponResponse;
import com.petshop.backend.entity.coupon.Coupon;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.ConflictException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.coupon.CouponRepository;
import com.petshop.backend.service.coupon.CouponService;
import com.petshop.backend.util.MoneyUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse applyCoupon(ApplyCouponRequest request) {
        BigDecimal total = request.getCartTotal() == null ? BigDecimal.ZERO : request.getCartTotal();
        Applied applied = validate(request.getCode(), total);
        Coupon coupon = applied.coupon();
        return CouponResponse.builder()
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .discountAmount(applied.discount())
                .finalTotal(MoneyUtils.scale(total.subtract(applied.discount()).max(BigDecimal.ZERO)))
                .valid(true)
                .message("Coupon applied. Discount: " + applied.discount() + " AZN")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Applied validate(String code, BigDecimal subtotal) {
        if (code == null || code.isBlank()) {
            throw new BadRequestException("COUPON_REQUIRED", "Coupon code is required");
        }
        String clean = code.trim().toUpperCase();
        Coupon coupon = couponRepository.findByCodeIgnoreCase(clean)
                .orElseThrow(() -> new BadRequestException("COUPON_NOT_FOUND", "Coupon not found: " + clean));
        if (Boolean.FALSE.equals(coupon.getActive())) {
            throw new BadRequestException("COUPON_INACTIVE", "This coupon is inactive");
        }
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
            throw new BadRequestException("COUPON_NOT_STARTED", "This coupon is not active yet");
        }
        if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
            throw new BadRequestException("COUPON_EXPIRED", "This coupon has expired");
        }
        if (coupon.getUsageLimit() != null && coupon.getUsageCount() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new BadRequestException("COUPON_LIMIT_REACHED", "This coupon has reached its usage limit");
        }
        if (coupon.getMinCartAmount() != null && subtotal.compareTo(coupon.getMinCartAmount()) < 0) {
            throw new BadRequestException("COUPON_MIN_AMOUNT", "Minimum order amount for this coupon is "
                    + coupon.getMinCartAmount() + " AZN");
        }
        BigDecimal discount;
        if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
            discount = subtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                discount = coupon.getMaxDiscountAmount();
            }
        } else {
            discount = coupon.getDiscountValue().min(subtotal);
        }
        return new Applied(coupon, MoneyUtils.scale(discount));
    }

    @Override
    @Transactional
    public void incrementUsage(Long couponId) {
        couponRepository.findById(couponId).ifPresent(c -> {
            c.setUsageCount((c.getUsageCount() == null ? 0 : c.getUsageCount()) + 1);
            couponRepository.save(c);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponAdminResponse> getAll() {
        return couponRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream().map(this::toAdmin).toList();
    }

    @Override
    @Transactional
    public CouponAdminResponse create(CouponRequest request) {
        if (couponRepository.findByCodeIgnoreCase(request.getCode().trim()).isPresent()) {
            throw new ConflictException("DUPLICATE_COUPON", "Coupon code already exists");
        }
        Coupon coupon = new Coupon();
        coupon.setUsageCount(0);
        apply(coupon, request);
        return toAdmin(couponRepository.save(coupon));
    }

    @Override
    @Transactional
    public CouponAdminResponse update(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", id));
        couponRepository.findByCodeIgnoreCase(request.getCode().trim())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> { throw new ConflictException("DUPLICATE_COUPON", "Coupon code already exists"); });
        apply(coupon, request);
        return toAdmin(couponRepository.save(coupon));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", id));
        couponRepository.delete(coupon);
    }

    private void apply(Coupon c, CouponRequest r) {
        if ("PERCENTAGE".equalsIgnoreCase(r.getDiscountType()) && r.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BadRequestException("INVALID_DISCOUNT", "Percentage discount cannot exceed 100");
        }
        if (r.getValidFrom() != null && r.getValidUntil() != null && r.getValidUntil().isBefore(r.getValidFrom())) {
            throw new BadRequestException("INVALID_DATES", "validUntil must be after validFrom");
        }
        c.setCode(r.getCode().trim().toUpperCase());
        c.setDiscountType(r.getDiscountType().toUpperCase());
        c.setDiscountValue(r.getDiscountValue());
        c.setMinCartAmount(r.getMinCartAmount());
        c.setMaxDiscountAmount(r.getMaxDiscountAmount());
        c.setValidFrom(r.getValidFrom());
        c.setValidUntil(r.getValidUntil());
        c.setUsageLimit(r.getUsageLimit());
        c.setActive(r.getActive() == null || r.getActive());
    }

    private CouponAdminResponse toAdmin(Coupon c) {
        return CouponAdminResponse.builder()
                .id(c.getId())
                .code(c.getCode())
                .discountType(c.getDiscountType())
                .discountValue(c.getDiscountValue())
                .minCartAmount(c.getMinCartAmount())
                .maxDiscountAmount(c.getMaxDiscountAmount())
                .validFrom(c.getValidFrom())
                .validUntil(c.getValidUntil())
                .usageLimit(c.getUsageLimit())
                .usageCount(c.getUsageCount())
                .active(c.getActive())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
