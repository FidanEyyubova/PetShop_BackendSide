package com.petshop.backend.dto.request.content;

import jakarta.validation.constraints.NotBlank;

public class AboutSectionRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String subtitle;
    private String imageUrl;
    private String description;

    public AboutSectionRequest() {}

    public AboutSectionRequest(String title, String subtitle, String imageUrl, String description) {
        this.title = title;
        this.subtitle = subtitle;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}