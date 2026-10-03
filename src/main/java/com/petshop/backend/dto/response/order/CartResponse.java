package com.petshop.backend.dto.response.order;

import com.petshop.backend.dto.response.common.CartItemResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private List<CartItemResponse> items;

    @Schema(description = "Sum of quantities (navbar badge)")
    private Integer totalItems;

    @Schema(description = "Sum of line subtotals at the price the customer pays")
    private BigDecimal subtotal;

    @Schema(description = "Savings compared with list prices")
    private BigDecimal discount;

    @Schema(description = "Shipping; each store ships separately and is free from the threshold")
    private BigDecimal shippingFee;

    @Schema(description = "subtotal + shippingFee")
    private BigDecimal total;

    @Schema(description = "Same as total (kept for older clients)")
    private BigDecimal totalAmount;

    private BigDecimal freeShippingThreshold;

    @Schema(description = "false when at least one line is unavailable; checkout would fail")
    private Boolean checkoutReady;
}
