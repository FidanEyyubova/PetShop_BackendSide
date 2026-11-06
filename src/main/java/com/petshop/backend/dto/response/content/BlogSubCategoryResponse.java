package com.petshop.backend.dto.response.content;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogSubCategoryResponse {
    private Long id;
    @Schema(example = "Nutrition")
    private String title;
    @Schema(example = "nutrition")
    private String slug;
    @Schema(description = "Number of posts")
    private long count;
    private LocalDateTime createdAt;

    @Schema(description = "az / en / ru versions of the text fields, for edit forms")
    private Map<String, Map<String, Object>> translations;
}
