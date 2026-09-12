package com.petshop.backend.dto.response.service;

import java.math.BigDecimal;

public class ServiceResponse {
    private Long id;
    private Long providerId;
    private String title;
    private String description;
    private BigDecimal basePrice;
    private Integer durationMinutes;
    private String address;
    private String amenities;
    private BigDecimal rating;
    private Boolean emergencyService;
    private Long categoryId;
    private Boolean active;

    public ServiceResponse() {
    }

    public ServiceResponse(Long id, Long providerId, String title, String description, BigDecimal basePrice, Integer durationMinutes, String address, String amenities, BigDecimal rating, Boolean emergencyService, Long categoryId, Boolean active) {
        this.id = id;
        this.providerId = providerId;
        this.title = title;
        this.description = description;
        this.basePrice = basePrice;
        this.durationMinutes = durationMinutes;
        this.address = address;
        this.amenities = amenities;
        this.rating = rating;
        this.emergencyService = emergencyService;
        this.categoryId = categoryId;
        this.active = active;
    }

    public static ServiceResponseBuilder builder() {
        return new ServiceResponseBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getName() { return title; }
    public void setName(String name) { this.title = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public BigDecimal getPrice() { return basePrice; }
    public void setPrice(BigDecimal price) { this.basePrice = price; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public Boolean getEmergencyService() { return emergencyService; }
    public void setEmergencyService(Boolean emergencyService) { this.emergencyService = emergencyService; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public static class ServiceResponseBuilder {
        private Long id;
        private Long providerId;
        private String title;
        private String description;
        private BigDecimal basePrice;
        private Integer durationMinutes;
        private String address;
        private String amenities;
        private BigDecimal rating;
        private Boolean emergencyService;
        private Long categoryId;
        private Boolean active;

        ServiceResponseBuilder() {
        }

        public ServiceResponseBuilder id(Long id) { this.id = id; return this; }
        public ServiceResponseBuilder providerId(Long providerId) { this.providerId = providerId; return this; }
        public ServiceResponseBuilder title(String title) { this.title = title; return this; }
        public ServiceResponseBuilder name(String name) { this.title = name; return this; }
        public ServiceResponseBuilder description(String description) { this.description = description; return this; }
        public ServiceResponseBuilder basePrice(BigDecimal basePrice) { this.basePrice = basePrice; return this; }
        public ServiceResponseBuilder price(BigDecimal price) { this.basePrice = price; return this; }
        public ServiceResponseBuilder durationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; return this; }
        public ServiceResponseBuilder address(String address) { this.address = address; return this; }
        public ServiceResponseBuilder amenities(String amenities) { this.amenities = amenities; return this; }
        public ServiceResponseBuilder rating(BigDecimal rating) { this.rating = rating; return this; }
        public ServiceResponseBuilder emergencyService(Boolean emergencyService) { this.emergencyService = emergencyService; return this; }
        public ServiceResponseBuilder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public ServiceResponseBuilder active(Boolean active) { this.active = active; return this; }

        public ServiceResponse build() {
            return new ServiceResponse(id, providerId, title, description, basePrice, durationMinutes, address, amenities, rating, emergencyService, categoryId, active);
        }
    }
}