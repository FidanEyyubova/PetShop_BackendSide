package com.petshop.backend.service.product;

import com.petshop.backend.common.ProductSort;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Normalised product query.
 *
 * @param categoryIds    null = no category filter; empty = nothing matches (unknown slug)
 * @param includeHidden  vendor/admin lists also show hidden products
 */
public record ProductFilter(
        String q,
        Collection<Long> categoryIds,
        Long subCategoryId,
        String petType,
        String brand,
        Long storeId,
        Long vendorId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Boolean inStock,
        BigDecimal minRating,
        Boolean active,
        boolean includeHidden,
        ProductSort sort
) {
    /** Facet dimensions that can be left out when computing counts. */
    public enum Dimension { CATEGORY, PET_TYPE, BRAND, PRICE }
}
