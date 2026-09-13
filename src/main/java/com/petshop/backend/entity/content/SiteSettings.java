package com.petshop.backend.entity.content;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "site_settings")
public class SiteSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instagram_url")
    private String instagramUrl;

    @Column(name = "tiktok_url")
    private String tiktokUrl;

    @Column(name = "facebook_url")
    private String facebookUrl;

    @Column(name = "x_url")
    private String xUrl;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public SiteSettings() {}

    public SiteSettings(Long id, String instagramUrl, String tiktokUrl, String facebookUrl, String xUrl, String logoUrl, LocalDateTime updatedAt) {
        this.id = id;
        this.instagramUrl = instagramUrl;
        this.tiktokUrl = tiktokUrl;
        this.facebookUrl = facebookUrl;
        this.xUrl = xUrl;
        this.logoUrl = logoUrl;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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