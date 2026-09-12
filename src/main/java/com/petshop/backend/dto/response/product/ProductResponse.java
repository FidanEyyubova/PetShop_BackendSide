package com.petshop.backend.dto.response.product;

import java.math.BigDecimal;

public class ProductResponse {
    private Long id;
    private String title;
    private String slug;
    private String sku;
    private String brand;
    private String petType;
    private String categoryName;
    private Long categoryId;
    private Long storeId;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer stockQuantity;
    private String badges;
    private String sizes;
    private String flavors;
    private String images;
    private String description;
    private String keyBenefits;
    private String guarantees;
    private String ingredients;
    private String nutritionInfo;
    private String usageGuide;
    private BigDecimal rating;

    public ProductResponse() {
    }

    public ProductResponse(Long id, String title, String slug, String sku, String brand, String petType, String categoryName, Long categoryId, Long storeId, BigDecimal price, BigDecimal discountPrice, Integer stockQuantity, String badges, String sizes, String flavors, String images, String description, String keyBenefits, String guarantees, String ingredients, String nutritionInfo, String usageGuide, BigDecimal rating) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.sku = sku;
        this.brand = brand;
        this.petType = petType;
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.storeId = storeId;
        this.price = price;
        this.discountPrice = discountPrice;
        this.stockQuantity = stockQuantity;
        this.badges = badges;
        this.sizes = sizes;
        this.flavors = flavors;
        this.images = images;
        this.description = description;
        this.keyBenefits = keyBenefits;
        this.guarantees = guarantees;
        this.ingredients = ingredients;
        this.nutritionInfo = nutritionInfo;
        this.usageGuide = usageGuide;
        this.rating = rating;
    }

    public static ProductResponseBuilder builder() {
        return new ProductResponseBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(BigDecimal discountPrice) { this.discountPrice = discountPrice; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getBadges() { return badges; }
    public void setBadges(String badges) { this.badges = badges; }

    public String getSizes() { return sizes; }
    public void setSizes(String sizes) { this.sizes = sizes; }

    public String getFlavors() { return flavors; }
    public void setFlavors(String flavors) { this.flavors = flavors; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

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

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public static class ProductResponseBuilder {
        private Long id;
        private String title;
        private String slug;
        private String sku;
        private String brand;
        private String petType;
        private String categoryName;
        private Long categoryId;
        private Long storeId;
        private BigDecimal price;
        private BigDecimal discountPrice;
        private Integer stockQuantity;
        private String badges;
        private String sizes;
        private String flavors;
        private String images;
        private String description;
        private String keyBenefits;
        private String guarantees;
        private String ingredients;
        private String nutritionInfo;
        private String usageGuide;
        private BigDecimal rating;

        ProductResponseBuilder() {
        }

        public ProductResponseBuilder id(Long id) { this.id = id; return this; }
        public ProductResponseBuilder title(String title) { this.title = title; return this; }
        public ProductResponseBuilder slug(String slug) { this.slug = slug; return this; }
        public ProductResponseBuilder sku(String sku) { this.sku = sku; return this; }
        public ProductResponseBuilder brand(String brand) { this.brand = brand; return this; }
        public ProductResponseBuilder petType(String petType) { this.petType = petType; return this; }
        public ProductResponseBuilder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public ProductResponseBuilder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public ProductResponseBuilder storeId(Long storeId) { this.storeId = storeId; return this; }
        public ProductResponseBuilder price(BigDecimal price) { this.price = price; return this; }
        public ProductResponseBuilder discountPrice(BigDecimal discountPrice) { this.discountPrice = discountPrice; return this; }
        public ProductResponseBuilder stockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; return this; }
        public ProductResponseBuilder badges(String badges) { this.badges = badges; return this; }
        public ProductResponseBuilder sizes(String sizes) { this.sizes = sizes; return this; }
        public ProductResponseBuilder flavors(String flavors) { this.flavors = flavors; return this; }
        public ProductResponseBuilder images(String images) { this.images = images; return this; }
        public ProductResponseBuilder description(String description) { this.description = description; return this; }
        public ProductResponseBuilder keyBenefits(String keyBenefits) { this.keyBenefits = keyBenefits; return this; }
        public ProductResponseBuilder guarantees(String guarantees) { this.guarantees = guarantees; return this; }
        public ProductResponseBuilder ingredients(String ingredients) { this.ingredients = ingredients; return this; }
        public ProductResponseBuilder nutritionInfo(String nutritionInfo) { this.nutritionInfo = nutritionInfo; return this; }
        public ProductResponseBuilder usageGuide(String usageGuide) { this.usageGuide = usageGuide; return this; }
        public ProductResponseBuilder rating(BigDecimal rating) { this.rating = rating; return this; }

        public ProductResponse build() {
            return new ProductResponse(id, title, slug, sku, brand, petType, categoryName, categoryId, storeId, price, discountPrice, stockQuantity, badges, sizes, flavors, images, description, keyBenefits, guarantees, ingredients, nutritionInfo, usageGuide, rating);
        }
    }
}