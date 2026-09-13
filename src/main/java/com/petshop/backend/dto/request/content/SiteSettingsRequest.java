package com.petshop.backend.dto.request.content;

public class SiteSettingsRequest {
    private String instagramUrl;
    private String tiktokUrl;
    private String facebookUrl;
    private String xUrl;
    private String logoUrl;

    public SiteSettingsRequest() {}

    public SiteSettingsRequest(String instagramUrl, String tiktokUrl, String facebookUrl, String xUrl, String logoUrl) {
        this.instagramUrl = instagramUrl;
        this.tiktokUrl = tiktokUrl;
        this.facebookUrl = facebookUrl;
        this.xUrl = xUrl;
        this.logoUrl = logoUrl;
    }

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
}