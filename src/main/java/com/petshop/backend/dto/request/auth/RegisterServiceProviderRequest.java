package com.petshop.backend.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterServiceProviderRequest {
    @NotBlank(message = "Ad və soyad boş ola bilməz")
    private String fullName;

    @NotBlank(message = "Mərkəz adı mütləqdir")
    private String centerName;

    private String serviceType;
    private String logoUrl;

    @NotBlank(message = "E-poçt ünvanı mütləqdir")
    @Email(message = "Düzgün e-poçt daxil edin")
    private String email;

    private String phone;

    @NotBlank(message = "Şifrə boş ola bilməz")
    @Size(min = 6, message = "Şifrə minimum 6 simvol olmalıdır")
    private String password;

    private String confirmPassword;
}
