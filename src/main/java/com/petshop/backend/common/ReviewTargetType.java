package com.petshop.backend.common;

public enum ReviewTargetType {
    PRODUCT, STORE, SERVICE_PROVIDER;

    public static ReviewTargetType parse(String value) {
        if (value == null || value.isBlank()) return null;
        String v = value.trim().replace("-", "_").toUpperCase();
        return switch (v) {
            case "PRODUCT" -> PRODUCT;
            case "STORE", "SHOP", "VENDOR" -> STORE;
            case "SERVICE_PROVIDER", "SERVICECENTER", "SERVICE_CENTER", "SERVICE", "PROVIDER" -> SERVICE_PROVIDER;
            default -> throw new IllegalArgumentException("Unknown review target type: " + value
                    + ". Allowed: PRODUCT, STORE, SERVICE_PROVIDER");
        };
    }
}
