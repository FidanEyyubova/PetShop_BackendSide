package com.petshop.backend.entity.product;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String slug;
    private String sku;
    private String brand;

    @Column(name = "pet_type")
    private String petType;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "store_id")
    private Long storeId;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "discount_price")
    private BigDecimal discountPrice;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    private String badges;
    private String sizes;
    private String flavors;

    @Column(columnDefinition = "TEXT")
    private String images;

    // Tab 1: Overview
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "key_benefits", columnDefinition = "TEXT")
    private String keyBenefits;

    // Tab 2: Ingredients
    @Column(columnDefinition = "TEXT")
    private String guarantees;

    @Column(columnDefinition = "TEXT")
    private String ingredients;

    @Column(name = "nutrition_info", columnDefinition = "TEXT")
    private String nutritionInfo;

    // Tab 3: Usage
    @Column(name = "usage_guide", columnDefinition = "TEXT")
    private String usageGuide;

    private BigDecimal rating;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
