package com.petshop.backend.util;

public final class MaskUtils {

    private MaskUtils() {}

    public static String maskIban(String iban) {
        if (iban == null || iban.isBlank()) return iban;
        String clean = iban.replace(" ", "");
        if (clean.length() <= 8) return "****";
        return clean.substring(0, 4) + " **** **** " + clean.substring(clean.length() - 4);
    }

    public static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
