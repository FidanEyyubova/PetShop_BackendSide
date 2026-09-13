package com.petshop.backend.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {
    private String title;

    @NotBlank(message = "Full address is required")
    private String fullAddress;

    private String city;
    private Boolean isDefault;

    public AddressRequest() {}
    public AddressRequest(String title, String fullAddress, String city, Boolean isDefault) {
        this.title = title;
        this.fullAddress = fullAddress;
        this.city = city;
        this.isDefault = isDefault;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
}