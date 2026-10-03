package com.petshop.backend.config;

import com.petshop.backend.common.Roles;
import com.petshop.backend.common.UserStatus;
import com.petshop.backend.entity.coupon.Coupon;
import com.petshop.backend.entity.user.User;
import com.petshop.backend.repository.coupon.CouponRepository;
import com.petshop.backend.repository.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Seeds the admin account (only from ADMIN_EMAIL / ADMIN_PASSWORD, never hardcoded) and demo coupons.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties properties;

    public DataInitializer(UserRepository userRepository, CouponRepository couponRepository,
                           PasswordEncoder passwordEncoder, AppProperties properties) {
        this.userRepository = userRepository;
        this.couponRepository = couponRepository;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedCoupons();
    }

    private void seedAdmin() {
        String email = properties.getAdmin().getEmail();
        String password = properties.getAdmin().getPassword();
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            log.info("ADMIN_EMAIL / ADMIN_PASSWORD not set; no admin account seeded.");
            return;
        }
        if (userRepository.existsByEmailIgnoreCase(email.trim())) {
            return;
        }
        User admin = User.builder()
                .fullName("Administrator")
                .email(email.trim().toLowerCase())
                .password(passwordEncoder.encode(password))
                .role(Roles.ROLE_ADMIN)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(admin);
        log.info("Admin account created for {}", admin.getEmail());
    }

    private void seedCoupons() {
        if (couponRepository.count() > 0) return;
        couponRepository.save(Coupon.builder()
                .code("WELCOME10").discountType("PERCENTAGE").discountValue(BigDecimal.valueOf(10))
                .minCartAmount(BigDecimal.valueOf(20)).active(true).createdAt(LocalDateTime.now()).build());
        couponRepository.save(Coupon.builder()
                .code("SUMMER20").discountType("PERCENTAGE").discountValue(BigDecimal.valueOf(20))
                .minCartAmount(BigDecimal.valueOf(50)).maxDiscountAmount(BigDecimal.valueOf(30))
                .active(true).createdAt(LocalDateTime.now()).build());
        couponRepository.save(Coupon.builder()
                .code("PET10AZN").discountType("FIXED").discountValue(BigDecimal.valueOf(10))
                .minCartAmount(BigDecimal.valueOf(30)).active(true).createdAt(LocalDateTime.now()).build());
        log.info("Seeded demo coupons WELCOME10, SUMMER20, PET10AZN");
    }
}
