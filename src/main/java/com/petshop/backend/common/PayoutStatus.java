package com.petshop.backend.common;

public enum PayoutStatus {
    PENDING, APPROVED, COMPLETED, REJECTED;

    public static PayoutStatus parse(String value) {
        if (value == null || value.isBlank()) return null;
        String v = value.trim().toUpperCase();
        if (v.equals("SUCCESS")) return COMPLETED;
        try {
            return PayoutStatus.valueOf(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown payout status: " + value
                    + ". Allowed: PENDING, APPROVED, COMPLETED, REJECTED");
        }
    }
}
