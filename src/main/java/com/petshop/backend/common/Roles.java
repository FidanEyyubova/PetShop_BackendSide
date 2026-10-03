package com.petshop.backend.common;

/**
 * Single role vocabulary used by the whole application.
 * Authorities are stored with the ROLE_ prefix; {@code hasRole(...)} checks use the short names.
 */
public final class Roles {

    public static final String BUYER = "BUYER";
    public static final String VENDOR = "VENDOR";
    public static final String SERVICE_PROVIDER = "SERVICE_PROVIDER";
    public static final String ADMIN = "ADMIN";

    public static final String ROLE_BUYER = "ROLE_" + BUYER;
    public static final String ROLE_VENDOR = "ROLE_" + VENDOR;
    public static final String ROLE_SERVICE_PROVIDER = "ROLE_" + SERVICE_PROVIDER;
    public static final String ROLE_ADMIN = "ROLE_" + ADMIN;

    private Roles() {}

    /** Maps legacy and frontend role names to the canonical ROLE_* authority. */
    public static String normalize(String role) {
        if (role == null || role.isBlank()) return ROLE_BUYER;
        String r = role.trim().toUpperCase();
        if (r.startsWith("ROLE_")) r = r.substring(5);
        return switch (r) {
            case "SUPER_ADMIN", "ADMIN" -> ROLE_ADMIN;
            case "SELLER", "VENDOR" -> ROLE_VENDOR;
            case "SERVICE", "PROVIDER", "SERVICE_PROVIDER" -> ROLE_SERVICE_PROVIDER;
            default -> ROLE_BUYER;
        };
    }
}
