package com.petshop.backend.dto.response.service;

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
}
