package com.petshop.backend.dto.request.order;

import com.petshop.backend.common.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    @Schema(description = "One of my saved addresses; or send shippingAddress as text")
    private Long addressId;

    @Size(max = 500)
    @Schema(description = "Free-text address when addressId is not used", example = "Nizami küç. 10, mənzil 21, Bakı")
    private String shippingAddress;

    @Size(max = 100)
    @Schema(description = "Recipient; defaults to the account name")
    private String customerName;

    @Pattern(regexp = ValidationPatterns.PHONE, message = ValidationPatterns.PHONE_MESSAGE)
    @Schema(description = "Recipient phone; defaults to the account phone")
    private String customerPhone;

    @Schema(description = "CARD_ONLINE or CASH_ON_DELIVERY (cardOnline / cashOnDelivery accepted)", example = "CASH_ON_DELIVERY")
    private String paymentMethod;

    @Size(max = 64)
    @Schema(description = "Optional coupon code", example = "WELCOME10")
    private String couponCode;

    @Size(max = 1000)
    private String note;

    @Valid
    @Size(max = 100, message = "At most 100 lines per order")
    @Schema(description = "Optional explicit lines (e.g. buy now / reorder). Omit to order the cart.")
    private List<OrderItemRequest> items;
}
