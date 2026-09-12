package com.petshop.backend.dto.response.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponse {
    private Long id;
    private Long providerId;
    private String title;
    private String description;
    private BigDecimal basePrice;
    private Integer durationMinutes;
    private String address;
    private String amenities;
    private BigDecimal rating;
    private Boolean emergencyService;
}
