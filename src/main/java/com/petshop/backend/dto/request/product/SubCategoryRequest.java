package com.petshop.backend.dto.request.product;

import com.petshop.backend.i18n.TranslatableRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public class SubCategoryRequest implements TranslatableRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String icon;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @Schema(description = TRANSLATIONS_DOC + " Fields: title")
    private Map<String, Map<String, Object>> translations;

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

    @Override
    public Map<String, Map<String, Object>> getTranslations() { return translations; }
    public void setTranslations(Map<String, Map<String, Object>> translations) { this.translations = translations; }
}
