package com.petshop.backend.dto.response.content;

import java.util.Map;

import java.time.LocalDateTime;

public class AdminServiceResponse {

    private Long id;
    private String title;
    private String subtitle;
    private String imageUrl;
    private Boolean active;
    private LocalDateTime createdAt;

    private Map<String, Map<String, Object>> translations;

    public AdminServiceResponse() {}

    public AdminServiceResponse(Long id, String title, String subtitle, String imageUrl, Boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.imageUrl = imageUrl;
        this.active = active;
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

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Map<String, Map<String, Object>> getTranslations() { return translations; }
    public void setTranslations(Map<String, Map<String, Object>> translations) { this.translations = translations; }
}
