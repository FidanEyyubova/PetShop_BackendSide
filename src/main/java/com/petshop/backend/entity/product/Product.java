package com.petshop.backend.entity.product;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long vendorId;
    private Long storeId;
    private Long categoryId;
    private Long subCategoryId;

    @Column(nullable = false)
    private String title;

    private String slug;
    private String sku;
    private String brand;
    private String petType;

    @Column(nullable = false)
    private BigDecimal price;

    private BigDecimal discountPrice;
    private Integer stockQuantity;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String images;

    private BigDecimal rating;
    private Integer reviewCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String categoryName;
    private String subCategoryName;

    private String badges;
    private String sizes;
    private String flavors;
    private String keyBenefits;
    private String guarantees;
    private String ingredients;
    private String nutritionInfo;
    private String usageGuide;

    public Product() {}

    public Product(Long id, Long vendorId, Long storeId, Long categoryId, Long subCategoryId, String title, String slug, String sku, String brand, String petType, BigDecimal price, BigDecimal discountPrice, Integer stockQuantity, String description, String images, BigDecimal rating, Integer reviewCount, Boolean isActive, LocalDateTime createdAt, String categoryName, String subCategoryName, String badges, String sizes, String flavors, String keyBenefits, String guarantees, String ingredients, String nutritionInfo, String usageGuide) {
        this.id = id;
        this.vendorId = vendorId;
        this.storeId = storeId;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.title = title;
        this.slug = slug;
        this.sku = sku;
        this.brand = brand;
        this.petType = petType;
        this.price = price;
        this.discountPrice = discountPrice;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.images = images;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.categoryName = categoryName;
        this.subCategoryName = subCategoryName;
        this.badges = badges;
        this.sizes = sizes;
        this.flavors = flavors;
        this.keyBenefits = keyBenefits;
        this.guarantees = guarantees;
        this.ingredients = ingredients;
        this.nutritionInfo = nutritionInfo;
        this.usageGuide = usageGuide;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.isActive == null) this.isActive = true;
        if (this.rating == null) this.rating = BigDecimal.ZERO;
        if (this.reviewCount == null) this.reviewCount = 0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Long getSubCategoryId() { return subCategoryId; }
    public void setSubCategoryId(Long subCategoryId) { this.subCategoryId = subCategoryId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getPetType() { return petType; }
    public void setPetType(String petType) { this.petType = petType; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(BigDecimal discountPrice) { this.discountPrice = discountPrice; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getSubCategoryName() { return subCategoryName; }
    public void setSubCategoryName(String subCategoryName) { this.subCategoryName = subCategoryName; }
    public String getBadges() { return badges; }
    public void setBadges(String badges) { this.badges = badges; }
    public String getSizes() { return sizes; }
    public void setSizes(String sizes) { this.sizes = sizes; }
    public String getFlavors() { return flavors; }
    public void setFlavors(String flavors) { this.flavors = flavors; }
    public String getKeyBenefits() { return keyBenefits; }
    public void setKeyBenefits(String keyBenefits) { this.keyBenefits = keyBenefits; }
    public String getGuarantees() { return guarantees; }
    public void setGuarantees(String guarantees) { this.guarantees = guarantees; }
    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }
    public String getNutritionInfo() { return nutritionInfo; }
    public void setNutritionInfo(String nutritionInfo) { this.nutritionInfo = nutritionInfo; }
    public String getUsageGuide() { return usageGuide; }
    public void setUsageGuide(String usageGuide) { this.usageGuide = usageGuide; }

    public static ProductBuilder builder() { return new ProductBuilder(); }

    public static class ProductBuilder {
        private Long id;
        private Long vendorId;
        private Long storeId;
        private Long categoryId;
        private Long subCategoryId;
        private String title;
        private String slug;
        private String sku;
        private String brand;
        private String petType;
        private BigDecimal price;
        private BigDecimal discountPrice;
        private Integer stockQuantity;
        private String description;
        private String images;
        private BigDecimal rating;
        private Integer reviewCount;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private String categoryName;
        private String subCategoryName;
        private String badges;
        private String sizes;
        private String flavors;
        private String keyBenefits;
        private String guarantees;
        private String ingredients;
        private String nutritionInfo;
        private String usageGuide;

        public ProductBuilder id(Long id) { this.id = id; return this; }
        public ProductBuilder vendorId(Long vendorId) { this.vendorId = vendorId; return this; }
        public ProductBuilder storeId(Long storeId) { this.storeId = storeId; return this; }
        public ProductBuilder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public ProductBuilder subCategoryId(Long subCategoryId) { this.subCategoryId = subCategoryId; return this; }
        public ProductBuilder title(String title) { this.title = title; return this; }
        public ProductBuilder slug(String slug) { this.slug = slug; return this; }
        public ProductBuilder sku(String sku) { this.sku = sku; return this; }
        public ProductBuilder brand(String brand) { this.brand = brand; return this; }
        public ProductBuilder petType(String petType) { this.petType = petType; return this; }
        public ProductBuilder price(BigDecimal price) { this.price = price; return this; }
        public ProductBuilder discountPrice(BigDecimal discountPrice) { this.discountPrice = discountPrice; return this; }
        public ProductBuilder stockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; return this; }
        public ProductBuilder description(String description) { this.description = description; return this; }
        public ProductBuilder images(String images) { this.images = images; return this; }
        public ProductBuilder rating(BigDecimal rating) { this.rating = rating; return this; }
        public ProductBuilder reviewCount(Integer reviewCount) { this.reviewCount = reviewCount; return this; }
        public ProductBuilder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public ProductBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ProductBuilder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public ProductBuilder subCategoryName(String subCategoryName) { this.subCategoryName = subCategoryName; return this; }
        public ProductBuilder badges(String badges) { this.badges = badges; return this; }
        public ProductBuilder sizes(String sizes) { this.sizes = sizes; return this; }
        public ProductBuilder flavors(String flavors) { this.flavors = flavors; return this; }
        public ProductBuilder keyBenefits(String keyBenefits) { this.keyBenefits = keyBenefits; return this; }
        public ProductBuilder guarantees(String guarantees) { this.guarantees = guarantees; return this; }
        public ProductBuilder ingredients(String ingredients) { this.ingredients = ingredients; return this; }
        public ProductBuilder nutritionInfo(String nutritionInfo) { this.nutritionInfo = nutritionInfo; return this; }
        public ProductBuilder usageGuide(String usageGuide) { this.usageGuide = usageGuide; return this; }

        public Product build() {
            return new Product(id, vendorId, storeId, categoryId, subCategoryId, title, slug, sku, brand, petType, price, discountPrice, stockQuantity, description, images, rating, reviewCount, isActive, createdAt, categoryName, subCategoryName, badges, sizes, flavors, keyBenefits, guarantees, ingredients, nutritionInfo, usageGuide);
        }
    }
}