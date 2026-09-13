package com.petshop.backend.dto.request.product;

import jakarta.validation.constraints.NotBlank;

public class CategoryRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String icon;
    private String type;

    public CategoryRequest() {}

    public CategoryRequest(String title, String icon, String type) {
        this.title = title;
        this.icon = icon;
        this.type = type;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}