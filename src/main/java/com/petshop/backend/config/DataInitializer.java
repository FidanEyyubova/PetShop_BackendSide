package com.petshop.backend.config;

import com.petshop.backend.entity.user.User;
import com.petshop.backend.repository.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
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
    }
}