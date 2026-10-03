package com.petshop.backend.service.user;

import com.petshop.backend.config.AppProperties;
import com.petshop.backend.entity.auth.RefreshToken;
import com.petshop.backend.exception.UnauthorizedException;
import com.petshop.backend.repository.auth.RefreshTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

/** Opaque, rotating refresh tokens. Only SHA-256 hashes are stored. */
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final AppProperties.Jwt jwt;
    private final SecureRandom random = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository repository, AppProperties properties) {
        this.repository = repository;
        this.jwt = properties.getJwt();
    }

    public record IssuedToken(String rawToken, LocalDateTime expiresAt, Long userId) {}

    @Transactional
    public IssuedToken issue(Long userId, boolean rememberMe) {
        byte[] bytes = new byte[48];
        random.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        LocalDateTime expiresAt = rememberMe
                ? LocalDateTime.now().plusDays(jwt.getRefreshTokenDays())
                : LocalDateTime.now().plusHours(jwt.getShortRefreshTokenHours());
        repository.save(RefreshToken.builder()
                .userId(userId)
                .tokenHash(hash(raw))
                .expiresAt(expiresAt)
                .rememberMe(rememberMe)
                .build());
        return new IssuedToken(raw, expiresAt, userId);
    }

    /** Validates the token, revokes it and issues a new one with the same lifetime policy. */
    @Transactional(noRollbackFor = UnauthorizedException.class)
    public IssuedToken rotate(String rawToken) {
        RefreshToken token = repository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new UnauthorizedException("INVALID_REFRESH_TOKEN", "Invalid refresh token"));
        if (Boolean.TRUE.equals(token.getRevoked())) {
            // Re-use of a rotated token: treat as theft and end every session of this user.
            repository.revokeAllForUser(token.getUserId());
            throw new UnauthorizedException("INVALID_REFRESH_TOKEN", "Refresh token was already used");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("REFRESH_TOKEN_EXPIRED", "Refresh token expired, please sign in again");
        }
        token.setRevoked(true);
        repository.save(token);
        return issue(token.getUserId(), Boolean.TRUE.equals(token.getRememberMe()));
    }

    @Transactional
    public void revoke(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) return;
        repository.findByTokenHash(hash(rawToken)).ifPresent(t -> {
            t.setRevoked(true);
            repository.save(t);
        });
    }

    @Transactional
    public void revokeAll(Long userId) {
        repository.revokeAllForUser(userId);
    }

    @Scheduled(cron = "0 30 3 * * *")
    @Transactional
    public void purgeExpired() {
        repository.deleteExpired(LocalDateTime.now().minusDays(1));
    }

    private static String hash(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
