package com.petshop.backend.entity.content;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String targetType;
    private Long targetId;
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(columnDefinition = "TEXT")
    private String providerReply;

    private LocalDateTime createdAt;

    public Review() {}

    public Review(Long id, Long userId, String targetType, Long targetId, Integer rating, String comment, String providerReply, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.rating = rating;
        this.comment = comment;
        this.providerReply = providerReply;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getProviderReply() { return providerReply; }
    public void setProviderReply(String providerReply) { this.providerReply = providerReply; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static ReviewBuilder builder() { return new ReviewBuilder(); }

    public static class ReviewBuilder {
        private Long id;
        private Long userId;
        private String targetType;
        private Long targetId;
        private Integer rating;
        private String comment;
        private String providerReply;
        private LocalDateTime createdAt;

        public ReviewBuilder id(Long id) { this.id = id; return this; }
        public ReviewBuilder userId(Long userId) { this.userId = userId; return this; }
        public ReviewBuilder targetType(String targetType) { this.targetType = targetType; return this; }
        public ReviewBuilder targetId(Long targetId) { this.targetId = targetId; return this; }
        public ReviewBuilder rating(Integer rating) { this.rating = rating; return this; }
        public ReviewBuilder comment(String comment) { this.comment = comment; return this; }
        public ReviewBuilder providerReply(String providerReply) { this.providerReply = providerReply; return this; }
        public ReviewBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Review build() {
            return new Review(id, userId, targetType, targetId, rating, comment, providerReply, createdAt);
        }
    }
}