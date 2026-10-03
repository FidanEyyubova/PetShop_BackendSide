package com.petshop.backend.common;

/** Whitelisted product sort keys; anything else is rejected with 400. */
public enum ProductSort {
    FEATURED, NEWEST, PRICE_LOW, PRICE_HIGH, RATING, NAME_AZ, NAME_ZA, BEST_SELLING;

    public static ProductSort parse(String value) {
        if (value == null || value.isBlank()) return NEWEST;
        String v = value.trim().replace('-', '_').toUpperCase();
        return switch (v) {
            case "FEATURED" -> FEATURED;
            case "NEWEST", "CREATEDAT", "CREATED_AT" -> NEWEST;
            case "PRICE_LOW", "PRICE_ASC", "PRICE" -> PRICE_LOW;
            case "PRICE_HIGH", "PRICE_DESC" -> PRICE_HIGH;
            case "RATING", "TOP_RATED" -> RATING;
            case "NAME_AZ", "NAME", "TITLE" -> NAME_AZ;
            case "NAME_ZA" -> NAME_ZA;
            case "BEST_SELLING", "BESTSELLERS", "SALES", "POPULAR" -> BEST_SELLING;
            default -> throw new IllegalArgumentException("Unknown sort: " + value
                    + ". Allowed: featured, newest, price-low, price-high, rating, name-az, name-za, best-selling");
        };
    }
}
