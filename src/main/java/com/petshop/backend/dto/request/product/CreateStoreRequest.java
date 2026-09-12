package com.petshop.backend.dto.request.product;

import jakarta.validation.constraints.NotBlank;

public class CreateStoreRequest {

    @NotBlank(message = "MaÄŸaza adÄ± boÅŸ ola bilmÉ™z")
    private String name;

    private String description;

    @NotBlank(message = "Ãœnvan boÅŸ ola bilmÉ™z")
    private String address;

    @NotBlank(message = "ÆlaqÉ™ nÃ¶mrÉ™si boÅŸ ola bilmÉ™z")
    private String phone;

    private String email;

    private String logoUrl;

    private String bannerUrl;

    public CreateStoreRequest() {
    }

    public CreateStoreRequest(String name, String description, String address, String phone, String email, String logoUrl, String bannerUrl) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.logoUrl = logoUrl;
        this.bannerUrl = bannerUrl;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
}