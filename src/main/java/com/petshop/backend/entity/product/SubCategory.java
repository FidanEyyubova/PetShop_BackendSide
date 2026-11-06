package com.petshop.backend.entity.product;

import com.petshop.backend.i18n.Translations;
import com.petshop.backend.i18n.TranslationsConverter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sub_categories")
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String icon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Convert(converter = TranslationsConverter.class)
    @Column(columnDefinition = "TEXT")
    private Translations translations = Translations.empty();

    public SubCategory() {}

    public SubCategory(Long id, String title, String icon, Category category, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.category = category;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Translations getTranslations() { return translations == null ? Translations.empty() : translations; }
    public void setTranslations(Translations translations) { this.translations = translations; }
}