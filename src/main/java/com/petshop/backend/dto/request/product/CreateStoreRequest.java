package com.petshop.backend.dto.request.product;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.petshop.backend.common.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** Vendor-editable public store profile. Platform fields (verified, rating, responseRate) are not accepted. */
@Data
public class CreateStoreRequest {

    @NotBlank(message = "Store name is required")
    @Size(min = 2, max = 150)
    @Schema(example = "PetLand")
    private String name;

    @Size(max = 200)
    @Schema(example = "Everything your pet needs")
    private String tagline;

    @JsonAlias("about")
    @Size(max = 5000)
    private String description;

    @JsonAlias("logo")
    @Pattern(regexp = ValidationPatterns.IMAGE_URL, message = ValidationPatterns.IMAGE_URL_MESSAGE)
    @Size(max = 1000)
    private String logoUrl;

    @JsonAlias("bannerImage")
    @Pattern(regexp = ValidationPatterns.IMAGE_URL, message = ValidationPatterns.IMAGE_URL_MESSAGE)
    @Size(max = 1000)
    private String bannerUrl;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = ValidationPatterns.PHONE, message = ValidationPatterns.PHONE_MESSAGE)
    private String phone;

    @Pattern(regexp = ValidationPatterns.PHONE, message = ValidationPatterns.PHONE_MESSAGE)
    private String altPhone;

    @Pattern(regexp = ValidationPatterns.PHONE, message = ValidationPatterns.PHONE_MESSAGE)
    private String whatsapp;

    @Size(max = 300)
    @Schema(description = "Instagram handle (@petshop) or profile URL")
    private String instagram;

    @Email(message = "Enter a valid email address")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Address is required")
    @Size(max = 300)
    private String address;

    @Size(max = 100)
    private String city;

    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @Schema(example = "40.3777")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @Schema(example = "49.8920")
    private Double longitude;

    @Size(max = 255)
    @Schema(example = "Mon-Sat 09:00-20:00")
    private String workingHours;

    @Size(max = 2000)
    private String deliveryInfo;

    @Size(max = 20)
    private List<@Size(max = 200) String> features = new ArrayList<>();

    @Pattern(regexp = ValidationPatterns.VOEN, message = ValidationPatterns.VOEN_MESSAGE)
    private String voen;
}
