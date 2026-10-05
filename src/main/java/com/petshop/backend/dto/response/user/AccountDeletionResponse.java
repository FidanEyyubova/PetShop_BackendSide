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
public class AccountDeletionResponse {
    private Long id;
    private Long userId;

    @Schema(description = "Role at deletion time", example = "ROLE_BUYER")
    private String role;

    @Schema(description = "Name and email before anonymisation")
    private String fullName;
    private String email;

    @Schema(description = "NOT_USING, SWITCHING, PRIVACY, BAD_EXPERIENCE or OTHER; null when an admin deleted the account",
            example = "NOT_USING")
    private String reason;

    @Schema(description = "Free-text explanation written by the user")
    private String comment;

    @Schema(description = "SELF or ADMIN", example = "SELF")
    private String deletedBy;

    private LocalDateTime createdAt;
}
