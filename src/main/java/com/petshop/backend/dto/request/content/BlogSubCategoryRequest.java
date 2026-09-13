package com.petshop.backend.dto.request.content;

import jakarta.validation.constraints.NotBlank;

public class BlogSubCategoryRequest {

    @NotBlank(message = "Title is required")
    private String title;

    public BlogSubCategoryRequest() {}

    public BlogSubCategoryRequest(String title) {
        this.title = title;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}