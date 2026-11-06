package com.petshop.backend.dto.response.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreResponse {
    private Long id;
    private Long ownerId;
    private String name;
    private String slug;
    private String tagline;
    private String logoUrl;
    private String bannerUrl;
    private String description;
    private String phone;
    private String altPhone;
    private String whatsapp;
    private String instagram;
    private String email;
    private String address;
    private String city;
    private Double latitude;
    private Double longitude;
    @Schema(description = "Same as latitude/longitude, in the frontend's shape")
    private Coords coords;
    private String workingHours;
    private String deliveryInfo;
    private List<String> features;

    @Schema(description = "Platform-owned: set by admins")
    private Boolean verified;
    private String badgeText;
    private Integer responseRate;

    private BigDecimal rating;
    private Integer reviewCount;
    @Schema(description = "Active products in the store")
    private Long productCount;

    @Schema(description = "Store creation date (\"joined\")")
    private LocalDateTime joinedDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Tax id; only returned to the owner and admins")
    private String voen;

    @Schema(description = "az / en / ru versions of the text fields, for edit forms")
    private Map<String, Map<String, Object>> translations;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Coords {
        private Double lat;
        private Double lng;
    }
}
