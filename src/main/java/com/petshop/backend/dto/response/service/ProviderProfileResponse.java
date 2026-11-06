package com.petshop.backend.dto.response.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderProfileResponse {
    private Long providerId;
    private String fullName;
    private String centerName;
    private String serviceType;
    private String serviceSlug;
    private String bio;
    private String city;
    private String district;
    private String address;
    private String googleMapsUrl;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String whatsapp;
    private String instagram;
    private String bannerUrl;
    private String logoUrl;
    private List<String> amenities;
    private List<String> tags;

    private BigDecimal rating;
    private Integer reviewCount;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private String priceRange;
    private Boolean isOpen;

    private List<ServiceResponse> services;
    @Schema(description = "Public page: featured members (or all when none are featured); owner: all members")
    private List<TeamMemberResponse> teamMembers;
    private List<ScheduleResponse> schedules;
    @Schema(description = "Monday..Sunday -> \"09:00 - 20:00\" or \"Closed\"")
    private Map<String, String> weeklySchedule;
    private List<ProviderGalleryResponse> gallery;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Owner/admin only")
    private String email;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Owner/admin only: ACTIVE or PENDING_APPROVAL")
    private String accountStatus;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Owner/admin only: percentage of filled profile fields")
    private Integer profileCompleteness;

    @Schema(description = "az / en / ru versions of the text fields, for edit forms")
    private Map<String, Map<String, Object>> translations;
}
