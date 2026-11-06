package com.petshop.backend.service.product;

import com.petshop.backend.common.ProductSort;
import com.petshop.backend.entity.product.Product;
import com.petshop.backend.util.MaskUtils;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ProductSpecifications {

    private ProductSpecifications() {}

    public static Expression<BigDecimal> effectivePrice(Root<Product> root, CriteriaBuilder cb) {
        Path<BigDecimal> price = root.get("price");
        Path<BigDecimal> discount = root.get("discountPrice");
        return cb.<BigDecimal>selectCase()
                .when(cb.and(cb.isNotNull(discount), cb.greaterThan(discount, BigDecimal.ZERO), cb.lessThan(discount, price)), discount)
                .otherwise(price);
    }

    public static List<Predicate> predicates(Root<Product> root, CriteriaBuilder cb, ProductFilter f,
                                             ProductFilter.Dimension skip) {
        List<Predicate> p = new ArrayList<>();
        p.add(cb.isNull(root.get("deletedAt")));
        if (!f.includeHidden()) {
            p.add(cb.isTrue(root.get("isActive")));
        } else if (f.active() != null) {
            p.add(cb.equal(root.get("isActive"), f.active()));
        }

        if (f.q() != null && !f.q().isBlank()) {
            String pattern = "%" + MaskUtils.escapeLike(f.q().trim().toLowerCase(Locale.ROOT)) + "%";
            p.add(cb.or(
                    cb.like(cb.lower(root.get("title")), pattern, '\\'),
                    cb.like(cb.lower(cb.coalesce(root.get("brand"), "")), pattern, '\\'),
                    cb.like(cb.lower(cb.coalesce(root.get("categoryName"), "")), pattern, '\\'),
                    cb.like(cb.lower(cb.coalesce(root.get("sku"), "")), pattern, '\\'),
                    cb.like(cb.lower(cb.coalesce(root.get("translationsText"), "")), pattern, '\\')));
        }
        if (skip != ProductFilter.Dimension.CATEGORY && f.categoryIds() != null) {
            p.add(f.categoryIds().isEmpty() ? cb.disjunction() : root.get("categoryId").in(f.categoryIds()));
        }
        if (f.subCategoryId() != null) {
            p.add(cb.equal(root.get("subCategoryId"), f.subCategoryId()));
        }
        if (skip != ProductFilter.Dimension.PET_TYPE && f.petType() != null) {
            p.add(cb.equal(root.get("petType"), f.petType()));
        }
        if (skip != ProductFilter.Dimension.BRAND && f.brand() != null && !f.brand().isBlank()) {
            p.add(cb.equal(cb.lower(root.get("brand")), f.brand().trim().toLowerCase(Locale.ROOT)));
        }
        if (f.storeId() != null) p.add(cb.equal(root.get("storeId"), f.storeId()));
        if (f.vendorId() != null) p.add(cb.equal(root.get("vendorId"), f.vendorId()));
        if (skip != ProductFilter.Dimension.PRICE) {
            if (f.minPrice() != null) p.add(cb.greaterThanOrEqualTo(effectivePrice(root, cb), f.minPrice()));
            if (f.maxPrice() != null) p.add(cb.lessThanOrEqualTo(effectivePrice(root, cb), f.maxPrice()));
        }
        if (Boolean.TRUE.equals(f.inStock())) {
            p.add(cb.greaterThan(root.get("stockQuantity"), 0));
        } else if (Boolean.FALSE.equals(f.inStock())) {
            p.add(cb.or(cb.isNull(root.get("stockQuantity")), cb.lessThanOrEqualTo(root.get("stockQuantity"), 0)));
        }
        if (f.minRating() != null) p.add(cb.greaterThanOrEqualTo(root.get("rating"), f.minRating()));
        return p;
    }

    public static Specification<Product> of(ProductFilter f) {
        return (root, query, cb) -> {
            List<Predicate> predicates = predicates(root, cb, f, null);
            Class<?> resultType = query.getResultType();
            if (resultType != Long.class && resultType != long.class) {
                query.orderBy(orders(root, cb, f.sort()));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static List<Order> orders(Root<Product> root, CriteriaBuilder cb, ProductSort sort) {
        List<Order> o = new ArrayList<>();
        switch (sort == null ? ProductSort.NEWEST : sort) {
            case PRICE_LOW -> o.add(cb.asc(effectivePrice(root, cb)));
            case PRICE_HIGH -> o.add(cb.desc(effectivePrice(root, cb)));
            case RATING -> {
                o.add(cb.desc(root.get("rating")));
                o.add(cb.desc(root.get("reviewCount")));
            }
            case NAME_AZ -> o.add(cb.asc(cb.lower(root.get("title"))));
            case NAME_ZA -> o.add(cb.desc(cb.lower(root.get("title"))));
            case BEST_SELLING, FEATURED -> {
                o.add(cb.desc(root.get("soldCount")));
                o.add(cb.desc(root.get("rating")));
            }
            case NEWEST -> { }
        }
        o.add(cb.desc(root.get("createdAt")));
        o.add(cb.desc(root.get("id")));
        return o;
    }
}
