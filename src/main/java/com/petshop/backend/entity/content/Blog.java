package com.petshop.backend.entity.content;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blogs")
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long authorId;

    @Column(nullable = false)
    private String title;

    private String slug;
    private String excerpt;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String coverImage;
    private String tags;
    private String readTime;
    private LocalDateTime createdAt;

    public Blog() {}

    public Blog(Long id, Long authorId, String title, String slug, String excerpt, String content, String coverImage, String tags, String readTime, LocalDateTime createdAt) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.slug = slug;
        this.excerpt = excerpt;
        this.content = content;
        this.coverImage = coverImage;
        this.tags = tags;
        this.readTime = readTime;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getExcerpt() { return excerpt; }
    public void setExcerpt(String excerpt) { this.excerpt = excerpt; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getReadTime() { return readTime; }
    public void setReadTime(String readTime) { this.readTime = readTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static BlogBuilder builder() { return new BlogBuilder(); }

    public static class BlogBuilder {
        private Long id;
        private Long authorId;
        private String title;
        private String slug;
        private String excerpt;
        private String content;
        private String coverImage;
        private String tags;
        private String readTime;
        private LocalDateTime createdAt;

        public BlogBuilder id(Long id) { this.id = id; return this; }
        public BlogBuilder authorId(Long authorId) { this.authorId = authorId; return this; }
        public BlogBuilder title(String title) { this.title = title; return this; }
        public BlogBuilder slug(String slug) { this.slug = slug; return this; }
        public BlogBuilder excerpt(String excerpt) { this.excerpt = excerpt; return this; }
        public BlogBuilder content(String content) { this.content = content; return this; }
        public BlogBuilder coverImage(String coverImage) { this.coverImage = coverImage; return this; }
        public BlogBuilder tags(String tags) { this.tags = tags; return this; }
        public BlogBuilder readTime(String readTime) { this.readTime = readTime; return this; }
        public BlogBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Blog build() {
            return new Blog(id, authorId, title, slug, excerpt, content, coverImage, tags, readTime, createdAt);
        }
    }
}