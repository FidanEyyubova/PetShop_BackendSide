package com.petshop.backend.dto.response.vendor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Vendor overview KPI tiles")
public class VendorDashboardResponse {
    @Schema(description = "Non-cancelled order totals this calendar month")
    private BigDecimal revenueThisMonth;
    private BigDecimal revenueLastMonth;
    @Schema(description = "Change vs last month in percent; null when last month had no revenue", example = "18.4")
    private BigDecimal revenueChangePercent;
    @Schema(description = "Orders in pending, processing or shipping")
    private long activeOrders;
    @Schema(description = "Orders waiting to be accepted (pending / new)")
    private long newOrders;
    @Schema(description = "Units sold in non-cancelled orders, all time")
    private long soldProducts;
    private long totalProducts;
    @Schema(description = "Products with 0 < stock <= 5")
    private long lowStockProducts;
    private BigDecimal rating;
    private Integer reviewCount;
    @Schema(description = "ACTIVE or PENDING_APPROVAL")
    private String accountStatus;
    private Boolean storeVerified;
}
