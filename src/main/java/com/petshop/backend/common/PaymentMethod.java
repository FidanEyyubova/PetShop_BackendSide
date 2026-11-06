package com.petshop.backend.common;

public enum PaymentMethod {
    CARD_ONLINE, CASH_ON_DELIVERY;

    public static PaymentMethod parse(String value) {
        if (value == null || value.isBlank()) return CASH_ON_DELIVERY;
        String v = value.trim().replace("-", "_").replace(" ", "_").toUpperCase();
        return switch (v) {
            case "CARDONLINE", "CARD_ONLINE", "ONLINE", "CARD" -> CARD_ONLINE;
            case "CASHONDELIVERY", "CASH_ON_DELIVERY", "CASH", "COD" -> CASH_ON_DELIVERY;
            default -> throw new IllegalArgumentException("Unknown payment method: " + value
                    + ". Allowed: CARD_ONLINE, CASH_ON_DELIVERY");
        };
    }
}
