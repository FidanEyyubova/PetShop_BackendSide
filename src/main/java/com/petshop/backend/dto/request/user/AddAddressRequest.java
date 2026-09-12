package com.petshop.backend.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddAddressRequest {
    @NotBlank(message = "Ünvan başlığı boş ola bilməz (məs: Ev, İş)")
    private String title;

    @NotBlank(message = "Tam ünvan boş ola bilməz")
    private String fullAddress;

    private String city;
    private Boolean isDefault;
}
