package com.petshop.backend.entity.product;

import com.petshop.backend.i18n.Translations;
import com.petshop.backend.i18n.TranslationsConverter;
import com.petshop.backend.util.StringListConverter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_id")
    private Long vendorId;

    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "sub_category_id")
    private Long subCategoryId;

    @Column(nullable = false)
    private String title;

    private String slug;
    private String sku;
    private String brand;

    @Column(name = "pet_type")
    private String petType;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "discount_price")
    private BigDecimal discountPrice;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Convert(converter = StringListConverter.class)
    @Column(name = "images", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> images = new ArrayList<>();

    private BigDecimal rating;

    @Column(name = "review_count")
    private Integer reviewCount;

    @Column(name = "sold_count")
    private Integer soldCount;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "sub_category_name")
    private String subCategoryName;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private List<String> badges = new ArrayList<>();

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private List<String> sizes = new ArrayList<>();

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private List<String> flavors = new ArrayList<>();

    @Convert(converter = StringListConverter.class)
    @Column(name = "key_benefits", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> keyBenefits = new ArrayList<>();

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private List<String> guarantees = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String ingredients;

    @Column(name = "nutrition_info", columnDefinition = "TEXT")
    private String nutritionInfo;

    @Column(name = "usage_guide", columnDefinition = "TEXT")
    private String usageGuide;

    @Convert(converter = TranslationsConverter.class)
    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private Translations translations = Translations.empty();

    @Column(name = "translations", columnDefinition = "TEXT", insertable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private String translationsText;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Version
    @Column(name = "version")
    private Long version;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (isActive == null) isActive = true;
        if (rating == null) rating = BigDecimal.ZERO;
        if (reviewCount == null) reviewCount = 0;
        if (soldCount == null) soldCount = 0;
        if (stockQuantity == null) stockQuantity = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isPubliclyVisible() {
        return deletedAt == null && !Boolean.FALSE.equals(isActive);
    }
}
