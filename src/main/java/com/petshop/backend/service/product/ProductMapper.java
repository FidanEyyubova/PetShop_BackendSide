package com.petshop.backend.service.product;

import com.petshop.backend.config.AppProperties;
import com.petshop.backend.dto.common.ProductIngredients;
import com.petshop.backend.dto.common.ProductUsageGuide;
import com.petshop.backend.dto.response.product.ProductCardResponse;
import com.petshop.backend.dto.response.product.ProductResponse;
import com.petshop.backend.entity.product.Category;
import com.petshop.backend.entity.product.Product;
import com.petshop.backend.entity.product.Store;
import com.petshop.backend.repository.product.CategoryRepository;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.repository.product.StoreRepository;
import com.petshop.backend.util.JsonUtils;
import com.petshop.backend.util.MoneyUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Maps products to API responses. Store names, category slugs and best-seller flags are loaded
 * once per batch to avoid N+1 queries.
 */
@Component
public class ProductMapper {

    public static final int BEST_SELLER_COUNT = 8;

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final int lowStockThreshold;

    public ProductMapper(StoreRepository storeRepository, CategoryRepository categoryRepository,
                         ProductRepository productRepository, AppProperties properties) {
        this.storeRepository = storeRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.lowStockThreshold = properties.getFinance().getLowStockThreshold();
    }

    /** Lookup data for a batch of products. */
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
        return ProductCardResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .slug(p.getSlug())
                .brand(p.getBrand())
                .sku(p.getSku())
                .petType(p.getPetType())
                .categoryId(p.getCategoryId())
                .categoryName(category != null ? category.getName() : p.getCategoryName())
                .categorySlug(category != null ? category.getSlug() : null)
                .storeId(p.getStoreId())
                .storeName(store != null ? store.getName() : null)
                .price(MoneyUtils.scale(p.getPrice()))
                .discountPrice(discountOrNull(p))
                .finalPrice(MoneyUtils.scale(MoneyUtils.effectivePrice(p.getPrice(), p.getDiscountPrice())))
                .discountPercent(MoneyUtils.discountPercent(p.getPrice(), p.getDiscountPrice()))
                .image(cover(p))
                .badges(p.getBadges())
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
        return ProductResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .slug(p.getSlug())
                .sku(p.getSku())
                .brand(p.getBrand())
                .petType(p.getPetType())
                .categoryId(p.getCategoryId())
                .categoryName(category != null ? category.getName() : p.getCategoryName())
                .categorySlug(category != null ? category.getSlug() : null)
                .subCategoryId(p.getSubCategoryId())
                .subCategoryName(p.getSubCategoryName())
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
                .badges(p.getBadges())
                .sizes(p.getSizes())
                .flavors(p.getFlavors())
                .description(p.getDescription())
                .keyBenefits(p.getKeyBenefits())
                .guarantees(p.getGuarantees())
                .ingredients(readIngredients(p.getIngredients()))
                .nutritionInfo(p.getNutritionInfo())
                .usageGuide(readUsageGuide(p.getUsageGuide()))
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
                .build();
    }

    public static String cover(Product p) {
        return p.getImages() == null || p.getImages().isEmpty() ? null : p.getImages().get(0);
    }

    /** Reads the JSON object; legacy plain text becomes activeIngredients. */
    public static ProductIngredients readIngredients(String raw) {
        if (raw == null || raw.isBlank()) return null;
        if (raw.trim().startsWith("{")) {
            ProductIngredients parsed = JsonUtils.read(raw, ProductIngredients.class);
            if (parsed != null) return parsed;
        }
        return ProductIngredients.builder().activeIngredients(raw).build();
    }

    /** Reads the JSON object; legacy plain text becomes frequency. */
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
