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
@Schema(description = "Vendor finance tab. Earnings are counted from delivered orders minus the platform commission.")
public class VendorFinanceStatsResponse {
    @Schema(description = "Total of delivered orders (before commission)")
    private BigDecimal totalRevenue;
    @Schema(description = "Platform commission rate, e.g. 0.10 = 10%")
    private BigDecimal commissionRate;
    private BigDecimal commissionAmount;
    @Schema(description = "totalRevenue - commissionAmount")
    private BigDecimal netEarnings;
    @Schema(description = "Net earnings that can be withdrawn now")
    private BigDecimal availableBalance;
    @Schema(description = "Net value of orders still in progress (pending, processing, shipping)")
    private BigDecimal pendingBalance;
    @Schema(description = "Payout requests waiting for approval")
    private BigDecimal pendingPayouts;
    @Schema(description = "Approved and completed payouts")
    private BigDecimal totalWithdrawn;
    @Schema(description = "Units sold in non-cancelled orders")
    private Long totalSalesCount;
    private Long completedOrdersCount;
}
