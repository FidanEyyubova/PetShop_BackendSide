package com.petshop.backend.dto.request.common;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBlogRequest {
    @NotBlank(message = "Məqalə başlığı boş ola bilməz")
    private String title;

    private String excerpt;

    @NotBlank(message = "Məqalə mətni mütləqdir")
    private String content;

    private String coverImage;
    private String tags;
    private String readTime;
}
