package com.petshop.backend.dto.request.content;

import com.petshop.backend.i18n.TranslatableRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;

public class AdminServiceRequest implements TranslatableRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String subtitle;
    private String imageUrl;
    private Boolean active = true;

    @Schema(description = TRANSLATIONS_DOC + " Fields: title, subtitle")
    private Map<String, Map<String, Object>> translations;

    public AdminServiceRequest() {}

    public AdminServiceRequest(String title, String subtitle, String imageUrl, Boolean active) {
        this.title = title;
        this.subtitle = subtitle;
        this.imageUrl = imageUrl;
        this.active = active;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    @Override
    public Map<String, Map<String, Object>> getTranslations() { return translations; }
    public void setTranslations(Map<String, Map<String, Object>> translations) { this.translations = translations; }
}
