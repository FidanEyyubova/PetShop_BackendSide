package com.petshop.backend.dto.response.service;

public class TeamMemberResponse {
    private Long id;
    private String fullName;
    private String role;
    private String experience;
    private String note;
    private String avatar;

    public TeamMemberResponse() {
    }

    public TeamMemberResponse(Long id, String fullName, String role, String experience, String note, String avatar) {
        this.id = id;
        this.fullName = fullName;
        this.role = role;
        this.experience = experience;
        this.note = note;
        this.avatar = avatar;
    }

    public static TeamMemberResponseBuilder builder() {
        return new TeamMemberResponseBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public static class TeamMemberResponseBuilder {
        private Long id;
        private String fullName;
        private String role;
        private String experience;
        private String note;
        private String avatar;

        TeamMemberResponseBuilder() {
        }

        public TeamMemberResponseBuilder id(Long id) { this.id = id; return this; }
        public TeamMemberResponseBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public TeamMemberResponseBuilder role(String role) { this.role = role; return this; }
        public TeamMemberResponseBuilder experience(String experience) { this.experience = experience; return this; }
        public TeamMemberResponseBuilder note(String note) { this.note = note; return this; }
        public TeamMemberResponseBuilder avatar(String avatar) { this.avatar = avatar; return this; }

        public TeamMemberResponse build() {
            return new TeamMemberResponse(id, fullName, role, experience, note, avatar);
        }
    }
}