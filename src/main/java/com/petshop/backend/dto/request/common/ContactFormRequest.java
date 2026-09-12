package com.petshop.backend.dto.request.common;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContactFormRequest {
    @NotBlank(message = "Adınız boş ola bilməz")
    private String name;

    @NotBlank(message = "E-poçt ünvanı mütləqdir")
    @Email(message = "Düzgün e-poçt daxil edin")
    private String email;

    private String subject;

    @NotBlank(message = "Mesajınız boş ola bilməz")
    private String message;
}
