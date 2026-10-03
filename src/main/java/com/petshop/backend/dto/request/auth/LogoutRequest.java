package com.petshop.backend.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LogoutRequest {

    @Schema(description = "Refresh token to revoke. With allDevices=true (and an access token) every session is revoked.")
    private String refreshToken;

    private Boolean allDevices = false;
}
