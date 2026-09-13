package com.petshop.backend.dto.request.content;

import jakarta.validation.constraints.NotBlank;

public class AdminServiceRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String subtitle;
    private String imageUrl;
    private Boolean active = true;

    public AdminServiceRequest() {}

    public AdminServiceRequest(String title, String subtitle, String imageUrl, Boolean active) {
        this.title = title;
        this.subtitle = subtitle;
        this.imageUrl = imageUrl;
        this.active = active;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}