package com.petshop.backend.dto.response.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderSummaryResponse {
    @Schema(description = "Provider user id (use for /api/v1/providers/{id} and reviews)")
    private Long id;
    private String centerName;
    @Schema(example = "GROOMING")
    private String serviceType;
    @Schema(example = "grooming")
    private String serviceSlug;
    private String logoUrl;
    @Schema(description = "Cover / banner image")
    private String coverImage;
    private String description;
    private String city;
    private String district;
    private String address;
    private String phone;
    private String whatsapp;
    private String instagram;
    private BigDecimal rating;
    private Integer reviewCount;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    @Schema(example = "₼ 25 - 90")
    private String priceRange;
    @Schema(description = "Open now (Asia/Baku time) according to the weekly schedule")
    private Boolean isOpen;
    @Schema(description = "Today's hours", example = "09:00 - 20:00")
    private String workingHours;
    private List<String> tags;
}
