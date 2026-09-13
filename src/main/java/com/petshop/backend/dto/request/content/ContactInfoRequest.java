package com.petshop.backend.dto.request.content;

public class ContactInfoRequest {
    private String location;
    private String mail;
    private String phone;
    private String workingHours;
    private String locationMapUrl;

    public ContactInfoRequest() {}

    public ContactInfoRequest(String location, String mail, String phone, String workingHours, String locationMapUrl) {
        this.location = location;
        this.mail = mail;
        this.phone = phone;
        this.workingHours = workingHours;
        this.locationMapUrl = locationMapUrl;
    }

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
}