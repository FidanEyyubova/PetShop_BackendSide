package com.petshop.backend.dto.response.service;

import java.util.List;

public class ProviderProfileResponse {
    private Long providerId;
    private String fullName;
    private String phone;
    private String email;
    private String avatarUrl;
    private List<ScheduleResponse> schedules;
    private List<TeamMemberResponse> teamMembers;

    public ProviderProfileResponse() {
    }

    public ProviderProfileResponse(Long providerId, String fullName, String phone, String email, String avatarUrl, List<ScheduleResponse> schedules, List<TeamMemberResponse> teamMembers) {
        this.providerId = providerId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.schedules = schedules;
        this.teamMembers = teamMembers;
    }

    public static ProviderProfileResponseBuilder builder() {
        return new ProviderProfileResponseBuilder();
    }

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public List<ScheduleResponse> getSchedules() { return schedules; }
    public void setSchedules(List<ScheduleResponse> schedules) { this.schedules = schedules; }

    public List<TeamMemberResponse> getTeamMembers() { return teamMembers; }
    public void setTeamMembers(List<TeamMemberResponse> teamMembers) { this.teamMembers = teamMembers; }

    public static class ProviderProfileResponseBuilder {
        private Long providerId;
        private String fullName;
        private String phone;
        private String email;
        private String avatarUrl;
        private List<ScheduleResponse> schedules;
        private List<TeamMemberResponse> teamMembers;

        ProviderProfileResponseBuilder() {
        }

        public ProviderProfileResponseBuilder providerId(Long providerId) { this.providerId = providerId; return this; }
        public ProviderProfileResponseBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public ProviderProfileResponseBuilder phone(String phone) { this.phone = phone; return this; }
        public ProviderProfileResponseBuilder email(String email) { this.email = email; return this; }
        public ProviderProfileResponseBuilder avatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; return this; }
        public ProviderProfileResponseBuilder schedules(List<ScheduleResponse> schedules) { this.schedules = schedules; return this; }
        public ProviderProfileResponseBuilder teamMembers(List<TeamMemberResponse> teamMembers) { this.teamMembers = teamMembers; return this; }

        public ProviderProfileResponse build() {
            return new ProviderProfileResponse(providerId, fullName, phone, email, avatarUrl, schedules, teamMembers);
        }
    }
}