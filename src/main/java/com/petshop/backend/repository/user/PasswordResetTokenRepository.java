package com.petshop.backend.repository.user;

import com.petshop.backend.entity.user.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findTopByEmailAndCodeAndUsedFalseOrderByCreatedAtDesc(String email, String code);
    void deleteByEmail(String email);
}