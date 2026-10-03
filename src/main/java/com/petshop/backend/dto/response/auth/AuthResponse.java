package com.petshop.backend.dto.response.auth;

import com.petshop.backend.dto.response.user.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Tokens and the signed-in user")
public class AuthResponse {

    @Schema(description = "Access token (JWT). Send as `Authorization: Bearer <token>`")
    private String token;

    @Builder.Default
    private String type = "Bearer";

    @Schema(description = "Access token lifetime in seconds", example = "3600")
    private Long expiresIn;

    @Schema(description = "Opaque refresh token for POST /api/v1/auth/refresh-token; rotated on every use")
    private String refreshToken;

    private LocalDateTime refreshTokenExpiresAt;

    private Long userId;
    private String fullName;
    private String email;
    private String role;
    private String avatarUrl;
    private UserResponse user;
}
