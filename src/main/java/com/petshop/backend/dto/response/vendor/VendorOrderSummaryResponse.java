package com.petshop.backend.dto.response.vendor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Summary tiles of the vendor orders tab for one month")
public class VendorOrderSummaryResponse {
    @Schema(example = "2026-09")
    private String month;
    @Schema(description = "Sum of order totals excluding cancelled orders")
    private BigDecimal revenue;
    @Schema(description = "Orders excluding cancelled")
    private long orderCount;
    private BigDecimal averageOrderValue;
    private long deliveredCount;
    private long cancelledCount;
    @Schema(description = "Orders per status (PENDING, PROCESSING, SHIPPING, DELIVERED, CANCELLED)")
    private Map<String, Long> statusCounts;
}
