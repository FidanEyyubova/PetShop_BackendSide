package com.petshop.backend.dto.response.content;

import java.util.Map;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HeroSectionResponse {

    private Long id;
    private String title;
    private String subtitle;
    private String bgImageUrl;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Boolean active;
    private LocalDateTime createdAt;

    private Map<String, Map<String, Object>> translations;

    public HeroSectionResponse() {}

    public HeroSectionResponse(Long id, String title, String subtitle, String bgImageUrl, BigDecimal price, BigDecimal discountPrice, Boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.bgImageUrl = bgImageUrl;
        this.price = price;
        this.discountPrice = discountPrice;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Map<String, Map<String, Object>> getTranslations() { return translations; }
    public void setTranslations(Map<String, Map<String, Object>> translations) { this.translations = translations; }
}
