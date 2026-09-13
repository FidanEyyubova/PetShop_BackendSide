package com.petshop.backend.dto.response.content;

import java.time.LocalDateTime;

public class AboutSectionResponse {

    private Long id;
    private String title;
    private String subtitle;
    private String imageUrl;
    private String description;
    private LocalDateTime createdAt;

    public AboutSectionResponse() {}

    public AboutSectionResponse(Long id, String title, String subtitle, String imageUrl, String description, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.imageUrl = imageUrl;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}