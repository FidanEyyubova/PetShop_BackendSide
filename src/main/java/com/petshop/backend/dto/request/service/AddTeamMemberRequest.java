package com.petshop.backend.dto.request.service;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddTeamMemberRequest {
    @NotBlank(message = "Mütəxəssisin adı boş ola bilməz")
    private String fullName;

    private String role;
    private String experience;
    private String note;
    private String avatar;
}
