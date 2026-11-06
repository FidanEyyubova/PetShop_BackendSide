package com.petshop.backend.dto.request.content;

import com.petshop.backend.i18n.TranslatableRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class HeroSectionRequest implements TranslatableRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String subtitle;
    private String bgImageUrl;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Boolean active = true;

    @Schema(description = TRANSLATIONS_DOC + " Fields: title, subtitle")
    private Map<String, Map<String, Object>> translations;

    public HeroSectionRequest() {}

    public HeroSectionRequest(String title, String subtitle, String bgImageUrl, BigDecimal price, BigDecimal discountPrice, Boolean active) {
        this.title = title;
        this.subtitle = subtitle;
        this.bgImageUrl = bgImageUrl;
        this.price = price;
        this.discountPrice = discountPrice;
        this.active = active;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getBgImageUrl() { return bgImageUrl; }
    public void setBgImageUrl(String bgImageUrl) { this.bgImageUrl = bgImageUrl; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(BigDecimal discountPrice) { this.discountPrice = discountPrice; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    @Override
    public Map<String, Map<String, Object>> getTranslations() { return translations; }
    public void setTranslations(Map<String, Map<String, Object>> translations) { this.translations = translations; }
}
