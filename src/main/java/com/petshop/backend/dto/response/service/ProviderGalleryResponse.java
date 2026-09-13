package com.petshop.backend.dto.response.service;

import java.time.LocalDateTime;

public class ProviderGalleryResponse {
    private Long id;
    private Long providerId;
    private String imageUrl;
    private String title;
    private LocalDateTime createdAt;

    public ProviderGalleryResponse() {}
    public ProviderGalleryResponse(Long id, Long providerId, String imageUrl, String title, LocalDateTime createdAt) {
        this.id = id;
        this.providerId = providerId;
        this.imageUrl = imageUrl;
        this.title = title;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}