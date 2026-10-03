package com.petshop.backend.entity.service;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** One line of a provider's price list (e.g. "Full grooming, 60 min, 35-50 ₼"). */
@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** Lowest price (priceMin). */
    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    /** Highest price; null for a fixed price. */
    @Column(name = "price_max")
    private BigDecimal priceMax;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    /** Free-text duration as typed by the provider ("45-60 dəq"). */
    @Column(name = "duration_text")
    private String durationText;

    private String address;

    @Column(columnDefinition = "TEXT")
    private String amenities;

    private BigDecimal rating;

    @Column(name = "emergency_service")
    private Boolean emergencyService;

    private Boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (active == null) active = true;
        if (emergencyService == null) emergencyService = false;
    }
}
