package com.petshop.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret:MySuperSecretKeyForPetShopJWTTokenGeneration2026MasterKey!}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationInMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        String role = userPrincipal.getAuthorities().stream()
                .filter(auth -> auth != null)
                .map(auth -> auth.getAuthority())
                .findFirst()
                .orElse("ROLE_BUYER");

        return Jwts.builder()
                .setSubject(userPrincipal.getEmail())
                .claim("id", userPrincipal.getId())
                .claim("fullName", userPrincipal.getFullName())
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object idObj = claims.get("id");
        if (idObj instanceof Number number) {
            return number.longValue();
        }
        return null;
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            logger.error("YanlÄ±ÅŸ JWT imzasÄ±: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token vaxtÄ± bitib: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("DÉ™stÉ™klÉ™nmÉ™yÉ™n JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT davamlÄ±lÄ±ÄŸÄ± boÅŸdur: {}", e.getMessage());
        }
        return false;
    }
}