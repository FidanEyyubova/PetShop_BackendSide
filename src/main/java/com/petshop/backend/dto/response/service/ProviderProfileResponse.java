package com.petshop.backend.dto.response.service;

import java.util.List;

public class ProviderProfileResponse {
    private Long providerId;
    private String fullName;
    private String phone;
    private String email;
    private String avatarUrl;

    private String centerName;
    private String serviceType;
    private String bio;
    private String city;
    private String district;
    private String address;
    private String googleMapsUrl;
    private String whatsapp;
    private String instagram;
    private String bannerUrl;
    private String logoUrl;

    private List<ScheduleResponse> schedules;
    private List<TeamMemberResponse> teamMembers;
    private List<ProviderGalleryResponse> gallery;

    public ProviderProfileResponse() {}

    public ProviderProfileResponse(Long providerId, String fullName, String phone, String email, String avatarUrl, String centerName, String serviceType, String bio, String city, String district, String address, String googleMapsUrl, String whatsapp, String instagram, String bannerUrl, String logoUrl, List<ScheduleResponse> schedules, List<TeamMemberResponse> teamMembers, List<ProviderGalleryResponse> gallery) {
        this.providerId = providerId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.centerName = centerName;
        this.serviceType = serviceType;
        this.bio = bio;
        this.city = city;
        this.district = district;
        this.address = address;
        this.googleMapsUrl = googleMapsUrl;
        this.whatsapp = whatsapp;
        this.instagram = instagram;
        this.bannerUrl = bannerUrl;
        this.logoUrl = logoUrl;
        this.schedules = schedules;
        this.teamMembers = teamMembers;
        this.gallery = gallery;
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

    public String getCenterName() { return centerName; }
    public void setCenterName(String centerName) { this.centerName = centerName; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getGoogleMapsUrl() { return googleMapsUrl; }
    public void setGoogleMapsUrl(String googleMapsUrl) { this.googleMapsUrl = googleMapsUrl; }

    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }

    public String getInstagram() { return instagram; }
    public void setInstagram(String instagram) { this.instagram = instagram; }

    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public List<ScheduleResponse> getSchedules() { return schedules; }
    public void setSchedules(List<ScheduleResponse> schedules) { this.schedules = schedules; }

    public List<TeamMemberResponse> getTeamMembers() { return teamMembers; }
    public void setTeamMembers(List<TeamMemberResponse> teamMembers) { this.teamMembers = teamMembers; }

    public List<ProviderGalleryResponse> getGallery() { return gallery; }
    public void setGallery(List<ProviderGalleryResponse> gallery) { this.gallery = gallery; }

    public static class ProviderProfileResponseBuilder {
        private Long providerId;
        private String fullName;
        private String phone;
        private String email;
        private String avatarUrl;
        private String centerName;
        private String serviceType;
        private String bio;
        private String city;
        private String district;
        private String address;
        private String googleMapsUrl;
        private String whatsapp;
        private String instagram;
        private String bannerUrl;
        private String logoUrl;
        private List<ScheduleResponse> schedules;
        private List<TeamMemberResponse> teamMembers;
        private List<ProviderGalleryResponse> gallery;

        ProviderProfileResponseBuilder() {}

        public ProviderProfileResponseBuilder providerId(Long providerId) { this.providerId = providerId; return this; }
        public ProviderProfileResponseBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public ProviderProfileResponseBuilder phone(String phone) { this.phone = phone; return this; }
        public ProviderProfileResponseBuilder email(String email) { this.email = email; return this; }
        public ProviderProfileResponseBuilder avatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; return this; }
        public ProviderProfileResponseBuilder centerName(String centerName) { this.centerName = centerName; return this; }
        public ProviderProfileResponseBuilder serviceType(String serviceType) { this.serviceType = serviceType; return this; }
        public ProviderProfileResponseBuilder bio(String bio) { this.bio = bio; return this; }
        public ProviderProfileResponseBuilder city(String city) { this.city = city; return this; }
        public ProviderProfileResponseBuilder district(String district) { this.district = district; return this; }
        public ProviderProfileResponseBuilder address(String address) { this.address = address; return this; }
        public ProviderProfileResponseBuilder googleMapsUrl(String googleMapsUrl) { this.googleMapsUrl = googleMapsUrl; return this; }
        public ProviderProfileResponseBuilder whatsapp(String whatsapp) { this.whatsapp = whatsapp; return this; }
        public ProviderProfileResponseBuilder instagram(String instagram) { this.instagram = instagram; return this; }
        public ProviderProfileResponseBuilder bannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; return this; }
        public ProviderProfileResponseBuilder logoUrl(String logoUrl) { this.logoUrl = logoUrl; return this; }
        public ProviderProfileResponseBuilder schedules(List<ScheduleResponse> schedules) { this.schedules = schedules; return this; }
        public ProviderProfileResponseBuilder teamMembers(List<TeamMemberResponse> teamMembers) { this.teamMembers = teamMembers; return this; }
        public ProviderProfileResponseBuilder gallery(List<ProviderGalleryResponse> gallery) { this.gallery = gallery; return this; }

        public ProviderProfileResponse build() {
            return new ProviderProfileResponse(providerId, fullName, phone, email, avatarUrl, centerName, serviceType, bio, city, district, address, googleMapsUrl, whatsapp, instagram, bannerUrl, logoUrl, schedules, teamMembers, gallery);
        }
    }
}