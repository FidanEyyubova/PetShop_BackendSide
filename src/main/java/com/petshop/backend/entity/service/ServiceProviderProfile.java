package com.petshop.backend.entity.service;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_provider_profiles")
public class ServiceProviderProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_id", nullable = false, unique = true)
    private Long providerId;

    private String centerName;
    private String serviceType;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String city;
    private String district;
    private String address;
    private String googleMapsUrl;
    private String phone;
    private String whatsapp;
    private String instagram;
    private String bannerUrl;
    private String logoUrl;
    private LocalDateTime updatedAt;

    public ServiceProviderProfile() {}

    public ServiceProviderProfile(Long id, Long providerId, String centerName, String serviceType, String bio, String city, String district, String address, String googleMapsUrl, String phone, String whatsapp, String instagram, String bannerUrl, String logoUrl, LocalDateTime updatedAt) {
        this.id = id;
        this.providerId = providerId;
        this.centerName = centerName;
        this.serviceType = serviceType;
        this.bio = bio;
        this.city = city;
        this.district = district;
        this.address = address;
        this.googleMapsUrl = googleMapsUrl;
        this.phone = phone;
        this.whatsapp = whatsapp;
        this.instagram = instagram;
        this.bannerUrl = bannerUrl;
        this.logoUrl = logoUrl;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    @PreUpdate
    protected void onSave() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

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

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }

    public String getInstagram() { return instagram; }
    public void setInstagram(String instagram) { this.instagram = instagram; }

    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}