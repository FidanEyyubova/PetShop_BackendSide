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
    private String city;
    private String district;
    private String street;
    private String floor;
    private String note;
    private String fullAddress;
    private Boolean isDefault;
}
