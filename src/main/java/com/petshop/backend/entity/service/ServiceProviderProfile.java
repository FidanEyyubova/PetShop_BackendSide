package com.petshop.backend.entity.service;

import com.petshop.backend.util.StringListConverter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_provider_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceProviderProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_id", nullable = false, unique = true)
    private Long providerId;

    @Column(name = "center_name")
    private String centerName;

    /** GROOMING, VETERINARY or TRAINING. */
    @Column(name = "service_type")
    private String serviceType;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String city;
    private String district;
    private String address;

    @Column(name = "google_maps_url", columnDefinition = "TEXT")
    private String googleMapsUrl;

    private Double latitude;
    private Double longitude;

    private String phone;
    private String whatsapp;
    private String instagram;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(name = "logo_url")
    private String logoUrl;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private List<String> amenities = new ArrayList<>();

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    private BigDecimal rating;

    @Column(name = "review_count")
    private Integer reviewCount;

    @Column(name = "page_views")
    private Long pageViews;

    @Column(name = "contact_clicks")
    private Long contactClicks;

    @Column(name = "wishlist_adds")
    private Long wishlistAdds;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        updatedAt = LocalDateTime.now();
        if (rating == null) rating = BigDecimal.ZERO;
        if (reviewCount == null) reviewCount = 0;
        if (pageViews == null) pageViews = 0L;
        if (contactClicks == null) contactClicks = 0L;
        if (wishlistAdds == null) wishlistAdds = 0L;
    }
}
