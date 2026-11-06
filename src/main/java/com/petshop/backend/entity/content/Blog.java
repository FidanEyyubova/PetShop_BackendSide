package com.petshop.backend.entity.content;

import com.petshop.backend.util.StringListConverter;
import com.petshop.backend.i18n.Translations;
import com.petshop.backend.i18n.TranslationsConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "blogs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String excerpt;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "cover_image")
    private String coverImage;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Column(name = "read_time")
    private String readTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Convert(converter = TranslationsConverter.class)
    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private Translations translations = Translations.empty();

    @Column(name = "translations", columnDefinition = "TEXT", insertable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private String translationsText;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
