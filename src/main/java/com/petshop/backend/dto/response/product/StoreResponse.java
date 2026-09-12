package com.petshop.backend.dto.response.product;

import java.math.BigDecimal;

public class StoreResponse {
    private Long id;
    private Long ownerId;
    private String name;
    private String slug;
    private String logoUrl;
    private String bannerUrl;
    private String description;
    private String phone;
    private String email;
    private String address;
    private BigDecimal rating;

    public StoreResponse() {
    }

    public StoreResponse(Long id, Long ownerId, String name, String slug, String logoUrl, String bannerUrl, String description, String phone, String email, String address, BigDecimal rating) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.slug = slug;
        this.logoUrl = logoUrl;
        this.bannerUrl = bannerUrl;
        this.description = description;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.rating = rating;
    }

    public static StoreResponseBuilder builder() {
        return new StoreResponseBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public static class StoreResponseBuilder {
        private Long id;
        private Long ownerId;
        private String name;
        private String slug;
        private String logoUrl;
        private String bannerUrl;
        private String description;
        private String phone;
        private String email;
        private String address;
        private BigDecimal rating;

        StoreResponseBuilder() {
        }

        public StoreResponseBuilder id(Long id) { this.id = id; return this; }
        public StoreResponseBuilder ownerId(Long ownerId) { this.ownerId = ownerId; return this; }
        public StoreResponseBuilder name(String name) { this.name = name; return this; }
        public StoreResponseBuilder slug(String slug) { this.slug = slug; return this; }
        public StoreResponseBuilder logoUrl(String logoUrl) { this.logoUrl = logoUrl; return this; }
        public StoreResponseBuilder bannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; return this; }
        public StoreResponseBuilder description(String description) { this.description = description; return this; }
        public StoreResponseBuilder phone(String phone) { this.phone = phone; return this; }
        public StoreResponseBuilder email(String email) { this.email = email; return this; }
        public StoreResponseBuilder address(String address) { this.address = address; return this; }
        public StoreResponseBuilder rating(BigDecimal rating) { this.rating = rating; return this; }

        public StoreResponse build() {
            return new StoreResponse(id, ownerId, name, slug, logoUrl, bannerUrl, description, phone, email, address, rating);
        }
    }
}