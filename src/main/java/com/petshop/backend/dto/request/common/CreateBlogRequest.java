package com.petshop.backend.dto.request.common;

import com.petshop.backend.i18n.TranslatableRequest;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.petshop.backend.common.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateBlogRequest implements TranslatableRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    @Size(max = 1000)
    private String excerpt;

    @NotBlank(message = "Content is required")
    @Size(max = 100_000)
    @Schema(description = "Article body (plain text or Markdown; rendered as text by the frontend)")
    private String content;

    @JsonAlias("image")
    @Pattern(regexp = ValidationPatterns.IMAGE_URL, message = ValidationPatterns.IMAGE_URL_MESSAGE)
    @Size(max = 1000)
    private String coverImage;

    @Size(max = 15)
    private List<@Size(max = 50) String> tags = new ArrayList<>();

    @Size(max = 50)
    @Schema(description = "Optional; computed from the content when empty", example = "5 min read")
    private String readTime;

    @Schema(description = "Blog category id; or send categorySlug")
    private Long categoryId;

    private String categorySlug;

    @Schema(description = TRANSLATIONS_DOC + " Fields: title, excerpt, content, tags, readTime")
    private Map<String, Map<String, Object>> translations;
}
