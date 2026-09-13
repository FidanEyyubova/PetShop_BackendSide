package com.petshop.backend.dto.response.product;

import java.time.LocalDateTime;

public class SubCategoryResponse {

    private Long id;
    private String title;
    private String icon;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;

    public SubCategoryResponse() {}

    public SubCategoryResponse(Long id, String title, String icon, Long categoryId, String categoryName, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}