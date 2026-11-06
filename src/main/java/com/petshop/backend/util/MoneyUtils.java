package com.petshop.backend.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MoneyUtils {

    private MoneyUtils() {}

    public static BigDecimal scale(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2) : value.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public static BigDecimal effectivePrice(BigDecimal price, BigDecimal discountPrice) {
        if (price == null) return BigDecimal.ZERO;
        if (discountPrice != null && discountPrice.signum() > 0 && discountPrice.compareTo(price) < 0) {
            return discountPrice;
        }
        return price;
    }

    public static int discountPercent(BigDecimal price, BigDecimal discountPrice) {
        BigDecimal effective = effectivePrice(price, discountPrice);
        if (price == null || price.signum() <= 0 || effective.compareTo(price) >= 0) return 0;
        return price.subtract(effective)
                .multiply(BigDecimal.valueOf(100))
                .divide(price, 0, RoundingMode.HALF_UP)
                .intValue();
    }
}
