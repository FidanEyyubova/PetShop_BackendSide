package com.petshop.backend.dto.response.user;

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
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;

    @Schema(description = "Authority", example = "ROLE_BUYER")
    private String role;

    @Schema(description = "Frontend role name: customer, vendor, service or admin", example = "customer")
    private String accountType;

    @Schema(description = "ACTIVE, PENDING_APPROVAL, REJECTED or SUSPENDED", example = "ACTIVE")
    private String status;

    private String avatarUrl;
    private String cityAddress;

    @Schema(description = "Vendor's store id (vendors only)")
    private Long storeId;

    @Schema(description = "false for accounts created with Google sign-in (no password to confirm with)")
    private Boolean hasPassword;

    private LocalDateTime createdAt;
}
