package com.petshop.backend.dto.response.auth;

import com.petshop.backend.dto.response.user.UserResponse;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String fullName;
    private String email;
    private String role;
    private String avatarUrl;
    private UserResponse user;

    public AuthResponse() {}

    public AuthResponse(String token, String type, Long userId, String fullName, String email, String role, String avatarUrl, UserResponse user) {
        this.token = token;
        this.type = type != null ? type : "Bearer";
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.avatarUrl = avatarUrl;
        this.user = user;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public static class AuthResponseBuilder {
        private String token;
        private String type = "Bearer";
        private Long userId;
        private String fullName;
        private String email;
        private String role;
        private String avatarUrl;
        private UserResponse user;

        public AuthResponseBuilder token(String token) { this.token = token; return this; }
        public AuthResponseBuilder type(String type) { this.type = type; return this; }
        public AuthResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public AuthResponseBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public AuthResponseBuilder email(String email) { this.email = email; return this; }
        public AuthResponseBuilder role(String role) { this.role = role; return this; }
        public AuthResponseBuilder avatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; return this; }
        public AuthResponseBuilder user(UserResponse user) {
            this.user = user;
            if (user != null) {
                this.userId = user.getId();
                this.fullName = user.getFullName();
                this.email = user.getEmail();
                this.role = user.getRole();
                this.avatarUrl = user.getAvatarUrl();
            }
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(token, type, userId, fullName, email, role, avatarUrl, user);
        }
    }
}