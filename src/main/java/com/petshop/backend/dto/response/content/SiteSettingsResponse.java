package com.petshop.backend.dto.response.content;

import java.time.LocalDateTime;

public class SiteSettingsResponse {
    private Long id;
    private String instagramUrl;
    private String tiktokUrl;
    private String facebookUrl;
    private String xUrl;
    private String logoUrl;
    private LocalDateTime updatedAt;

    public SiteSettingsResponse() {}

    public SiteSettingsResponse(Long id, String instagramUrl, String tiktokUrl, String facebookUrl, String xUrl, String logoUrl, LocalDateTime updatedAt) {
        this.id = id;
        this.instagramUrl = instagramUrl;
        this.tiktokUrl = tiktokUrl;
        this.facebookUrl = facebookUrl;
        this.xUrl = xUrl;
        this.logoUrl = logoUrl;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInstagramUrl() { return instagramUrl; }
    public void setInstagramUrl(String instagramUrl) { this.instagramUrl = instagramUrl; }

    public String getTiktokUrl() { return tiktokUrl; }
    public void setTiktokUrl(String tiktokUrl) { this.tiktokUrl = tiktokUrl; }

    public String getFacebookUrl() { return facebookUrl; }
    public void setFacebookUrl(String facebookUrl) { this.facebookUrl = facebookUrl; }

    public String getxUrl() { return xUrl; }
    public void setxUrl(String xUrl) { this.xUrl = xUrl; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}