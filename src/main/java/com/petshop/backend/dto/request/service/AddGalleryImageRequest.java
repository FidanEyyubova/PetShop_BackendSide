package com.petshop.backend.dto.request.service;

import jakarta.validation.constraints.NotBlank;

public class AddGalleryImageRequest {
    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private String title;

    public AddGalleryImageRequest() {}
    public AddGalleryImageRequest(String imageUrl, String title) {
        this.imageUrl = imageUrl;
        this.title = title;
    }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}