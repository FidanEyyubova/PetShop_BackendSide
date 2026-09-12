package com.petshop.backend.dto.request.service;

import lombok.Data;

@Data
public class UpdateProviderProfileRequest {
    private String centerName;
    private String serviceType;
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
}
