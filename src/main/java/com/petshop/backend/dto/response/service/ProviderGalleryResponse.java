package com.petshop.backend.dto.response.service;

import java.util.Map;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderGalleryResponse {
    private Long id;
    private Long providerId;
    private String imageUrl;
    private String title;
    private LocalDateTime createdAt;

    @Schema(description = "az / en / ru versions of the text fields, for edit forms")
    private Map<String, Map<String, Object>> translations;
}
