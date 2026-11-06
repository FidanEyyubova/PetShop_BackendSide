package com.petshop.backend.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoryResponse {
    private Long id;
    private String title;
    private String icon;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private Map<String, Map<String, Object>> translations;
}
