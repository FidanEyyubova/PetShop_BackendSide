package com.petshop.backend.config;

import com.petshop.backend.entity.coupon.Coupon;
import com.petshop.backend.entity.user.User;
import com.petshop.backend.repository.coupon.CouponRepository;
import com.petshop.backend.repository.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, CouponRepository couponRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.couponRepository = couponRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("superadmin@petshop.az")) {
            User admin = User.builder()
                    .fullName("Super Admin")
                    .email("superadmin@petshop.az")
                    .password(passwordEncoder.encode("Petshop2026!"))
                    .phone("+994500000000")
                    .role("ROLE_SUPER_ADMIN")
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(admin);
            System.out.println(">>> Super Admin user created successfully: superadmin@petshop.az");
        }

        if (couponRepository.count() == 0) {
            Coupon c1 = Coupon.builder()
                    .code("WELCOME10")
                    .discountType("PERCENTAGE")
                    .discountValue(BigDecimal.valueOf(10.0))
                    .minCartAmount(BigDecimal.valueOf(20.0))
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            Coupon c2 = Coupon.builder()
                    .code("SUMMER20")
                    .discountType("PERCENTAGE")
                    .discountValue(BigDecimal.valueOf(20.0))
                    .minCartAmount(BigDecimal.valueOf(50.0))
                    .maxDiscountAmount(BigDecimal.valueOf(30.0))
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            Coupon c3 = Coupon.builder()
                    .code("PET10AZN")
                    .discountType("FIXED")
                    .discountValue(BigDecimal.valueOf(10.0))
                    .minCartAmount(BigDecimal.valueOf(30.0))
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            couponRepository.save(c1);
            couponRepository.save(c2);
            couponRepository.save(c3);
            System.out.println(">>> Seeded initial promo coupons: WELCOME10, SUMMER20, PET10AZN");
        }
    }
}