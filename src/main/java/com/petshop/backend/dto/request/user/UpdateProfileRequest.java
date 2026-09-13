package com.petshop.backend.dto.request.user;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String phone;
    private String avatarUrl;
    private String cityAddress;

    public UpdateProfileRequest() {}
    public UpdateProfileRequest(String fullName, String phone, String avatarUrl, String cityAddress) {
        this.fullName = fullName;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.cityAddress = cityAddress;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getCityAddress() { return cityAddress; }
    public void setCityAddress(String cityAddress) { this.cityAddress = cityAddress; }
}