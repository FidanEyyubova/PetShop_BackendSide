package com.petshop.backend.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "Köhnə şifrə mütləqdir")
    private String oldPassword;

    @NotBlank(message = "Yeni şifrə mütləqdir")
    @Size(min = 6, message = "Şifrə minimum 6 simvol olmalıdır")
    private String newPassword;

    private String confirmPassword;
}
