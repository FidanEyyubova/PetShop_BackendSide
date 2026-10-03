package com.petshop.backend.security;

import com.petshop.backend.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

/** Issues and validates short-lived access tokens (HS512). Refresh tokens are opaque, see RefreshTokenService. */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final int MIN_SECRET_LENGTH = 64;

    private final SecretKey signingKey;
    private final long accessTokenMillis;

    public JwtTokenProvider(AppProperties properties) {
        String secret = properties.getJwt().getSecret();
        if (secret == null || secret.length() < MIN_SECRET_LENGTH) {
            byte[] random = new byte[64];
            new SecureRandom().nextBytes(random);
            secret = Base64.getEncoder().encodeToString(random);
            log.warn("JWT_SECRET is not set or shorter than {} characters. A random secret was generated; "
                    + "tokens become invalid after a restart. Set JWT_SECRET in production.", MIN_SECRET_LENGTH);
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenMillis = properties.getJwt().getAccessTokenMinutes() * 60_000L;
    }

    public String generateToken(UserPrincipal principal) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(principal.getEmail())
                .claim("id", principal.getId())
                .claim("fullName", principal.getFullName())
                .claim("role", principal.getRole())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenMillis))
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public long getAccessTokenSeconds() {
        return accessTokenMillis / 1000;
    }

    public String getEmailFromToken(String token) {
        return parse(token).getSubject();
    }

    public Long getUserIdFromToken(String token) {
        Object id = parse(token).get("id");
        return id instanceof Number n ? n.longValue() : null;
    }

    public boolean validateToken(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Rejected JWT: {}", e.getMessage());
            return false;
        }
    }

    private Claims parse(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
    }
}
