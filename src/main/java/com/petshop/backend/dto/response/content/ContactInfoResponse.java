package com.petshop.backend.dto.response.content;

import java.util.Map;

import java.time.LocalDateTime;

public class ContactInfoResponse {
    private Long id;
    private String location;
    private String mail;
    private String phone;
    private String workingHours;
    private String locationMapUrl;
    private LocalDateTime updatedAt;

    private Map<String, Map<String, Object>> translations;

    public ContactInfoResponse() {}

    public ContactInfoResponse(Long id, String location, String mail, String phone, String workingHours, String locationMapUrl, LocalDateTime updatedAt) {
        this.id = id;
        this.location = location;
        this.mail = mail;
        this.phone = phone;
        this.workingHours = workingHours;
        this.locationMapUrl = locationMapUrl;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWorkingHours() { return workingHours; }
    public void setWorkingHours(String workingHours) { this.workingHours = workingHours; }

    public String getLocationMapUrl() { return locationMapUrl; }
    public void setLocationMapUrl(String locationMapUrl) { this.locationMapUrl = locationMapUrl; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Map<String, Map<String, Object>> getTranslations() { return translations; }
    public void setTranslations(Map<String, Map<String, Object>> translations) { this.translations = translations; }
}
