package com.petshop.backend.dto.request.service;

import com.petshop.backend.i18n.TranslatableRequest;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.petshop.backend.common.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class UpdateProviderProfileRequest implements TranslatableRequest {

    @JsonAlias("name")
    @Size(min = 2, max = 150)
    @Schema(example = "PawSpa Grooming")
    private String centerName;

    @JsonAlias("category")
    @Schema(description = "grooming, veterinary or training", example = "grooming")
    private String serviceType;

    @Size(max = 5000)
    private String bio;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String district;

    @Size(max = 300)
    private String address;

    @Pattern(regexp = ValidationPatterns.URL, message = ValidationPatterns.URL_MESSAGE)
    @Size(max = 1000)
    private String googleMapsUrl;

    @DecimalMin("-90.0") @DecimalMax("90.0")
    private Double latitude;

    @DecimalMin("-180.0") @DecimalMax("180.0")
    private Double longitude;

    @Pattern(regexp = ValidationPatterns.PHONE, message = ValidationPatterns.PHONE_MESSAGE)
    private String phone;

    @Pattern(regexp = ValidationPatterns.PHONE, message = ValidationPatterns.PHONE_MESSAGE)
    private String whatsapp;

    @Size(max = 300)
    @Schema(description = "Instagram handle (@pawspa) or profile URL")
    private String instagram;

    @JsonAlias({"banner", "coverImage"})
    @Pattern(regexp = ValidationPatterns.IMAGE_URL, message = ValidationPatterns.IMAGE_URL_MESSAGE)
    @Size(max = 1000)
    private String bannerUrl;

    @JsonAlias("logo")
    @Pattern(regexp = ValidationPatterns.IMAGE_URL, message = ValidationPatterns.IMAGE_URL_MESSAGE)
    @Size(max = 1000)
    private String logoUrl;

    @Size(max = 30)
    @Schema(description = "Amenity ids or labels (g1..g9, v1..v9, t1..t9)", example = "[\"g1\", \"g4\"]")
    private List<@Size(max = 100) String> amenities;

    @Size(max = 20)
    private List<@Size(max = 50) String> tags;

    @Schema(description = TRANSLATIONS_DOC + " Fields: bio, tags")
    private Map<String, Map<String, Object>> translations;
}
