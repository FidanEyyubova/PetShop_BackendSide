package com.petshop.backend.common;

import java.util.EnumSet;
import java.util.Set;

/**
 * Order lifecycle. The vendor dashboard calls PENDING "new".
 * PENDING -> PROCESSING -> SHIPPING -> DELIVERED, and CANCELLED from PENDING or PROCESSING.
 */
public enum OrderStatus {
    PENDING, PROCESSING, SHIPPING, DELIVERED, CANCELLED;

    public static final Set<OrderStatus> ACTIVE = EnumSet.of(PENDING, PROCESSING, SHIPPING);
    public static final Set<OrderStatus> CLOSED = EnumSet.of(DELIVERED, CANCELLED);

    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case PENDING -> next == PROCESSING || next == CANCELLED;
            case PROCESSING -> next == SHIPPING || next == CANCELLED;
            case SHIPPING -> next == DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }

    public boolean isCancellableByBuyer() {
        return this == PENDING || this == PROCESSING;
    }

    /** Accepts enum names in any case plus frontend aliases ("new", "shipped", "completed"). */
    public static OrderStatus parse(String value) {
        if (value == null || value.isBlank()) return null;
        String v = value.trim().toUpperCase();
        return switch (v) {
            case "NEW" -> PENDING;
            case "SHIPPED" -> SHIPPING;
            case "COMPLETED" -> DELIVERED;
            case "CANCELED" -> CANCELLED;
            default -> {
                try {
                    yield OrderStatus.valueOf(v);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Unknown order status: " + value
                            + ". Allowed: pending (new), processing, shipping, delivered, cancelled");
                }
            }
        };
    }
}
