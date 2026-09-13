package com.petshop.backend.dto.request.service;

import jakarta.validation.constraints.NotBlank;

public class AddTeamMemberRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    private String role;
    private String experience;
    private String note;
    private String avatar;

    public AddTeamMemberRequest() {}
    public AddTeamMemberRequest(String fullName, String role, String experience, String note, String avatar) {
        this.fullName = fullName;
        this.role = role;
        this.experience = experience;
        this.note = note;
        this.avatar = avatar;
    }

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
}