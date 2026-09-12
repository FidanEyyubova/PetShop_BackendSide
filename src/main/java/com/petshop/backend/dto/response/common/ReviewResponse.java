package com.petshop.backend.dto.response.common;

import java.time.LocalDateTime;

public class ReviewResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private String userAvatar;
    private String targetType;
    private Long targetId;
    private Integer rating;
    private String comment;
    private String providerReply;
    private LocalDateTime createdAt;

    public ReviewResponse() {
    }

    public ReviewResponse(Long id, Long userId, String userFullName, String userAvatar, String targetType, Long targetId, Integer rating, String comment, String providerReply, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userFullName = userFullName;
        this.userAvatar = userAvatar;
        this.targetType = targetType;
        this.targetId = targetId;
        this.rating = rating;
        this.comment = comment;
        this.providerReply = providerReply;
        this.createdAt = createdAt;
    }

    public static ReviewResponseBuilder builder() {
        return new ReviewResponseBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }

    public String getUserName() { return userFullName; }
    public void setUserName(String userName) { this.userFullName = userName; }

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

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

    public static class ReviewResponseBuilder {
        private Long id;
        private Long userId;
        private String userFullName;
        private String userAvatar;
        private String targetType;
        private Long targetId;
        private Integer rating;
        private String comment;
        private String providerReply;
        private LocalDateTime createdAt;

        ReviewResponseBuilder() {
        }

        public ReviewResponseBuilder id(Long id) { this.id = id; return this; }
        public ReviewResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public ReviewResponseBuilder userFullName(String userFullName) { this.userFullName = userFullName; return this; }
        public ReviewResponseBuilder userName(String userName) { this.userFullName = userName; return this; }
        public ReviewResponseBuilder userAvatar(String userAvatar) { this.userAvatar = userAvatar; return this; }
        public ReviewResponseBuilder targetType(String targetType) { this.targetType = targetType; return this; }
        public ReviewResponseBuilder targetId(Long targetId) { this.targetId = targetId; return this; }
        public ReviewResponseBuilder rating(Integer rating) { this.rating = rating; return this; }
        public ReviewResponseBuilder comment(String comment) { this.comment = comment; return this; }
        public ReviewResponseBuilder providerReply(String providerReply) { this.providerReply = providerReply; return this; }
        public ReviewResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ReviewResponse build() {
            return new ReviewResponse(id, userId, userFullName, userAvatar, targetType, targetId, rating, comment, providerReply, createdAt);
        }
    }
}