package com.petshop.backend.service.product;

import com.petshop.backend.config.AppProperties;
import com.petshop.backend.dto.common.ProductIngredients;
import com.petshop.backend.dto.common.ProductUsageGuide;
import com.petshop.backend.dto.response.product.ProductCardResponse;
import com.petshop.backend.dto.response.product.ProductResponse;
import com.petshop.backend.entity.product.Category;
import com.petshop.backend.entity.product.Product;
import com.petshop.backend.entity.product.Store;
import com.petshop.backend.entity.product.SubCategory;
import com.petshop.backend.i18n.Localized;
import com.petshop.backend.repository.product.CategoryRepository;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.repository.product.StoreRepository;
import com.petshop.backend.repository.product.SubCategoryRepository;
import com.petshop.backend.util.JsonUtils;
import com.petshop.backend.util.MoneyUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public static final int BEST_SELLER_COUNT = 8;

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final int lowStockThreshold;

    public ProductMapper(StoreRepository storeRepository, CategoryRepository categoryRepository,
                         ProductRepository productRepository, SubCategoryRepository subCategoryRepository,
                         AppProperties properties) {
        this.storeRepository = storeRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.lowStockThreshold = properties.getFinance().getLowStockThreshold();
    }

    public record Context(Map<Long, Store> stores, Map<Long, Category> categories, Set<Long> bestSellerIds) {}

    public Context context(Collection<Product> products) {
        Set<Long> storeIds = products.stream().map(Product::getStoreId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> categoryIds = products.stream().map(Product::getCategoryId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Store> stores = storeIds.isEmpty() ? Map.of()
                : storeRepository.findAllById(storeIds).stream().collect(Collectors.toMap(Store::getId, Function.identity()));
        Map<Long, Category> categories = categoryIds.isEmpty() ? Map.of()
                : categoryRepository.findAllById(categoryIds).stream().collect(Collectors.toMap(Category::getId, Function.identity()));
        return new Context(stores, categories, bestSellerIds());
    }

    public Set<Long> bestSellerIds() {
        return new HashSet<>(productRepository.findBestSellerIds(PageRequest.of(0, BEST_SELLER_COUNT)));
    }

    public List<ProductCardResponse> toCards(List<Product> products) {
        Context ctx = context(products);
        return products.stream().map(p -> toCard(p, ctx)).toList();
    }

    public ProductCardResponse toCard(Product p, Context ctx) {
        Store store = p.getStoreId() == null ? null : ctx.stores().get(p.getStoreId());
        Category category = p.getCategoryId() == null ? null : ctx.categories().get(p.getCategoryId());
        int stock = p.getStockQuantity() == null ? 0 : p.getStockQuantity();
        Localized t = Localized.of(p.getTranslations());
        return ProductCardResponse.builder()
                .id(p.getId())
                .title(t.text("title", p.getTitle()))
                .slug(p.getSlug())
                .brand(p.getBrand())
                .sku(p.getSku())
                .petType(p.getPetType())
                .categoryId(p.getCategoryId())
                .categoryName(category != null ? categoryName(category) : p.getCategoryName())
                .categorySlug(category != null ? category.getSlug() : null)
                .storeId(p.getStoreId())
                .storeName(store != null ? store.getName() : null)
                .price(MoneyUtils.scale(p.getPrice()))
                .discountPrice(discountOrNull(p))
                .finalPrice(MoneyUtils.scale(MoneyUtils.effectivePrice(p.getPrice(), p.getDiscountPrice())))
                .discountPercent(MoneyUtils.discountPercent(p.getPrice(), p.getDiscountPrice()))
                .image(cover(p))
                .badges(t.list("badges", p.getBadges()))
                .rating(p.getRating())
                .reviewCount(nz(p.getReviewCount()))
                .stockQuantity(stock)
                .inStock(stock > 0)
                .isBestSeller(ctx.bestSellerIds().contains(p.getId()))
                .soldCount(nz(p.getSoldCount()))
                .isActive(!Boolean.FALSE.equals(p.getIsActive()))
                .createdAt(p.getCreatedAt())
                .build();
    }

    public ProductResponse toDetail(Product p) {
        Context ctx = context(List.of(p));
        Store store = p.getStoreId() == null ? null : ctx.stores().get(p.getStoreId());
        Category category = p.getCategoryId() == null ? null : ctx.categories().get(p.getCategoryId());
        int stock = p.getStockQuantity() == null ? 0 : p.getStockQuantity();
        Localized t = Localized.of(p.getTranslations());
        SubCategory subCategory = p.getSubCategoryId() == null ? null
                : subCategoryRepository.findById(p.getSubCategoryId()).orElse(null);
        ProductIngredients ingredients = readIngredients(p.getIngredients());
        ProductUsageGuide usageGuide = readUsageGuide(p.getUsageGuide());
        return ProductResponse.builder()
                .id(p.getId())
                .title(t.text("title", p.getTitle()))
                .slug(p.getSlug())
                .sku(p.getSku())
                .brand(p.getBrand())
                .petType(p.getPetType())
                .categoryId(p.getCategoryId())
                .categoryName(category != null ? categoryName(category) : p.getCategoryName())
                .categorySlug(category != null ? category.getSlug() : null)
                .subCategoryId(p.getSubCategoryId())
                .subCategoryName(subCategory != null ? subCategoryName(subCategory) : p.getSubCategoryName())
                .storeId(p.getStoreId())
                .storeName(store != null ? store.getName() : null)
                .storeSlug(store != null ? store.getSlug() : null)
                .vendorId(p.getVendorId())
                .price(MoneyUtils.scale(p.getPrice()))
                .discountPrice(discountOrNull(p))
                .finalPrice(MoneyUtils.scale(MoneyUtils.effectivePrice(p.getPrice(), p.getDiscountPrice())))
                .discountPercent(MoneyUtils.discountPercent(p.getPrice(), p.getDiscountPrice()))
                .image(cover(p))
                .images(p.getImages())
                .badges(t.list("badges", p.getBadges()))
                .sizes(p.getSizes())
                .flavors(t.list("flavors", p.getFlavors()))
                .description(t.text("description", p.getDescription()))
                .keyBenefits(t.list("keyBenefits", p.getKeyBenefits()))
                .guarantees(t.list("guarantees", p.getGuarantees()))
                .ingredients(t.object("ingredients", ingredients, ProductIngredients.class))
                .nutritionInfo(t.text("nutritionInfo", p.getNutritionInfo()))
                .usageGuide(t.object("usageGuide", usageGuide, ProductUsageGuide.class))
                .stockQuantity(stock)
                .inStock(stock > 0)
                .lowStock(stock > 0 && stock <= lowStockThreshold)
                .rating(p.getRating())
                .reviewCount(nz(p.getReviewCount()))
                .soldCount(nz(p.getSoldCount()))
                .isBestSeller(ctx.bestSellerIds().contains(p.getId()))
                .isActive(!Boolean.FALSE.equals(p.getIsActive()))
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .translations(Localized.editable(p.getTranslations(),
                        "title", p.getTitle(), "description", p.getDescription(), "badges", p.getBadges(),
                        "flavors", p.getFlavors(), "keyBenefits", p.getKeyBenefits(), "guarantees", p.getGuarantees(),
                        "ingredients", ingredients, "nutritionInfo", p.getNutritionInfo(), "usageGuide", usageGuide))
                .build();
    }

    public static String categoryName(Category category) {
        return Localized.of(category.getTranslations()).text("title", category.getName());
    }

    public static String subCategoryName(SubCategory subCategory) {
        return Localized.of(subCategory.getTranslations()).text("title", subCategory.getTitle());
    }

    public static String localizedTitle(Product p) {
        return Localized.of(p.getTranslations()).text("title", p.getTitle());
    }

    public static String cover(Product p) {
        return p.getImages() == null || p.getImages().isEmpty() ? null : p.getImages().get(0);
    }

    public static ProductIngredients readIngredients(String raw) {
        if (raw == null || raw.isBlank()) return null;
        if (raw.trim().startsWith("{")) {
            ProductIngredients parsed = JsonUtils.read(raw, ProductIngredients.class);
            if (parsed != null) return parsed;
        }
        return ProductIngredients.builder().activeIngredients(raw).build();
    }

    public static ProductUsageGuide readUsageGuide(String raw) {
        if (raw == null || raw.isBlank()) return null;
        if (raw.trim().startsWith("{")) {
            ProductUsageGuide parsed = JsonUtils.read(raw, ProductUsageGuide.class);
            if (parsed != null) return parsed;
        }
        return ProductUsageGuide.builder().frequency(raw).build();
    }

    private static java.math.BigDecimal discountOrNull(Product p) {
        if (p.getPrice() == null) return null;
        boolean discounted = MoneyUtils.effectivePrice(p.getPrice(), p.getDiscountPrice()).compareTo(p.getPrice()) < 0;
        return discounted ? MoneyUtils.scale(p.getDiscountPrice()) : null;
    }

    private static int nz(Integer v) {
        return v == null ? 0 : v;
    }
}
