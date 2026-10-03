package com.petshop.backend.dto.response.vendor;

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
@Schema(description = "Revenue (bars) and order count (line) for the vendor chart")
public class SalesSeriesResponse {
    @Schema(description = "weekly (last 7 days) or monthly (this year)", example = "monthly")
    private String range;
    @Schema(example = "[\"Jan\", \"Feb\", \"Mar\"]")
    private List<String> labels;
    private List<BigDecimal> revenue;
    private List<Long> orders;
}
