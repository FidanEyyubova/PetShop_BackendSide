package com.petshop.backend.dto.request.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public class SubCategoryRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String icon;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    public SubCategoryRequest() {}

    public SubCategoryRequest(String title, String icon, Long categoryId) {
        this.title = title;
        this.icon = icon;
        this.categoryId = categoryId;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}