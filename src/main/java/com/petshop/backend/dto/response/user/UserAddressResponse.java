package com.petshop.backend.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAddressResponse {
    private Long id;
    private String title;
    private String fullAddress;
    private String city;
    private Boolean isDefault;
}
