package com.petshop.backend.entity.content;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact_info")
public class ContactInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;
    private String mail;
    private String phone;
    private String workingHours;

    @Column(name = "location_map_url", columnDefinition = "TEXT")
    private String locationMapUrl;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ContactInfo() {}

    public ContactInfo(Long id, String location, String mail, String phone, String workingHours, String locationMapUrl, LocalDateTime updatedAt) {
        this.id = id;
        this.location = location;
        this.mail = mail;
        this.phone = phone;
        this.workingHours = workingHours;
        this.locationMapUrl = locationMapUrl;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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
}