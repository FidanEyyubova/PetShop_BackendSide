package com.petshop.backend.dto.response.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogResponse {
    private Long id;
    private String title;
    private String slug;
    private String excerpt;
    @Schema(description = "Full body; omitted (null) in list responses")
    private String content;
    private String coverImage;
    private List<String> tags;
    @Schema(example = "5 min read")
    private String readTime;
    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
