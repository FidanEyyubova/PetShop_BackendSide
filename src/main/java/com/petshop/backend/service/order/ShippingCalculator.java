package com.petshop.backend.service.order;

import com.petshop.backend.config.AppProperties;
import com.petshop.backend.util.MoneyUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** "Free shipping on orders over 50 ₼": the fee applies per store order below the threshold. */
@Component
public class ShippingCalculator {

    private final AppProperties.Shipping shipping;

    public ShippingCalculator(AppProperties properties) {
        this.shipping = properties.getShipping();
    }

    public BigDecimal feeFor(BigDecimal storeSubtotal) {
        if (storeSubtotal == null || storeSubtotal.signum() <= 0) return MoneyUtils.scale(BigDecimal.ZERO);
        return storeSubtotal.compareTo(shipping.getFreeThreshold()) >= 0
                ? MoneyUtils.scale(BigDecimal.ZERO)
                : MoneyUtils.scale(shipping.getFee());
    }

    public BigDecimal freeThreshold() {
        return MoneyUtils.scale(shipping.getFreeThreshold());
    }
}
