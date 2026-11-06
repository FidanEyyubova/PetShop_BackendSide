package com.petshop.backend.dto.response.service;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse {
    private Long id;
    private Long providerId;
    private String title;
    private String description;
    private BigDecimal priceMin;
    @Schema(description = "null for a fixed price")
    private BigDecimal priceMax;
    @Schema(description = "Formatted price for display", example = "₼ 35 - 50")
    private String priceLabel;
    private String durationText;
    private Integer durationMinutes;
    private Boolean active;
    private Boolean emergencyService;
    private Long categoryId;
    private LocalDateTime createdAt;

    @Schema(description = "az / en / ru versions of the text fields, for edit forms")
    private Map<String, Map<String, Object>> translations;
}
