package com.petshop.backend.dto.request.user;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.petshop.backend.common.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    @JsonAlias("oldPassword")
    @Schema(description = "Also accepted as `oldPassword`")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Pattern(regexp = ValidationPatterns.PASSWORD, message = ValidationPatterns.PASSWORD_MESSAGE)
    private String newPassword;

    @Schema(description = "Optional; must match newPassword when sent")
    private String confirmPassword;
}
