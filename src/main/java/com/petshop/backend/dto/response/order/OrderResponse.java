package com.petshop.backend.dto.response.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    @Schema(example = "ORD-7K3P9Q2X")
    private String orderNumber;
    @Schema(description = "Orders created in the same checkout share this value")
    private String checkoutGroup;

    private Long buyerId;
    private String customerName;
    private String customerPhone;

    private Long storeId;
    private String storeName;
    private Long vendorId;

    @Schema(description = "PENDING (vendor UI: new), PROCESSING, SHIPPING, DELIVERED, CANCELLED")
    private String status;
    @Schema(description = "PENDING, PAID, FAILED, REFUNDED")
    private String paymentStatus;
    @Schema(description = "CARD_ONLINE or CASH_ON_DELIVERY")
    private String paymentMethod;

    private BigDecimal subtotal;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private String couponCode;
    @Schema(description = "subtotal + shippingFee - discountAmount")
    private BigDecimal totalAmount;

    private String shippingAddress;
    private String note;

    @Schema(description = "Delivery window shown in tracking")
    private LocalDateTime estimatedDeliveryFrom;
    private LocalDateTime estimatedDeliveryTo;
    private LocalDateTime deliveredAt;
    private String courierName;
    private String cancelReason;

    private Integer itemCount;
    private List<OrderItemResponse> items;
    @Schema(description = "Tracking timeline, oldest first")
    private List<OrderStatusHistoryResponse> statusHistory;

    @Schema(description = "true when the buyer may still cancel")
    private Boolean cancellable;
    private Boolean archivedByVendor;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
