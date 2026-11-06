package com.petshop.backend.entity.service;

import com.petshop.backend.i18n.Translations;
import com.petshop.backend.i18n.TranslationsConverter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "provider_galleries")
public class ProviderGallery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    private String title;
    private LocalDateTime createdAt;

    @Convert(converter = TranslationsConverter.class)
    @Column(columnDefinition = "TEXT")
    private Translations translations = Translations.empty();

    public ProviderGallery() {}

    public ProviderGallery(Long id, Long providerId, String imageUrl, String title, LocalDateTime createdAt) {
        this.id = id;
        this.providerId = providerId;
        this.imageUrl = imageUrl;
        this.title = title;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Translations getTranslations() { return translations == null ? Translations.empty() : translations; }
    public void setTranslations(Translations translations) { this.translations = translations; }
}
