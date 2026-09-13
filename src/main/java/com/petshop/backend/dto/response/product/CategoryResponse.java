package com.petshop.backend.dto.response.product;

import java.time.LocalDateTime;
import java.util.List;

public class CategoryResponse {

    private Long id;
    private String title;
    private String icon;
    private String slug;
    private String type;
    private List<SubCategoryResponse> subCategories;
    private LocalDateTime createdAt;

    public CategoryResponse() {}

    public CategoryResponse(Long id, String title, String icon, String slug, String type, List<SubCategoryResponse> subCategories, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.slug = slug;
        this.type = type;
        this.subCategories = subCategories;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<SubCategoryResponse> getSubCategories() { return subCategories; }
    public void setSubCategories(List<SubCategoryResponse> subCategories) { this.subCategories = subCategories; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}