package com.petshop.backend.dto.response.order;

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
@Schema(description = "Result of a checkout: one order per store")
public class CheckoutResponse {
    private String checkoutGroup;
    private List<OrderResponse> orders;
    private BigDecimal subtotal;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    @Schema(description = "Sum of all orders' totals")
    private BigDecimal grandTotal;
}
