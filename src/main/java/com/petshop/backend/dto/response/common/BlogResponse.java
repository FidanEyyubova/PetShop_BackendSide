package com.petshop.backend.dto.response.common;

import java.time.LocalDateTime;

public class BlogResponse {
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

    public BlogResponse() {
    }

    public BlogResponse(Long id, Long authorId, String title, String slug, String excerpt, String content, String coverImage, String tags, String readTime, LocalDateTime createdAt) {
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

    public static BlogResponseBuilder builder() {
        return new BlogResponseBuilder();
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

    public static class BlogResponseBuilder {
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

        BlogResponseBuilder() {
        }

        public BlogResponseBuilder id(Long id) { this.id = id; return this; }
        public BlogResponseBuilder authorId(Long authorId) { this.authorId = authorId; return this; }
        public BlogResponseBuilder title(String title) { this.title = title; return this; }
        public BlogResponseBuilder slug(String slug) { this.slug = slug; return this; }
        public BlogResponseBuilder excerpt(String excerpt) { this.excerpt = excerpt; return this; }
        public BlogResponseBuilder content(String content) { this.content = content; return this; }
        public BlogResponseBuilder coverImage(String coverImage) { this.coverImage = coverImage; return this; }
        public BlogResponseBuilder tags(String tags) { this.tags = tags; return this; }
        public BlogResponseBuilder readTime(String readTime) { this.readTime = readTime; return this; }
        public BlogResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public BlogResponse build() {
            return new BlogResponse(id, authorId, title, slug, excerpt, content, coverImage, tags, readTime, createdAt);
        }
    }
}