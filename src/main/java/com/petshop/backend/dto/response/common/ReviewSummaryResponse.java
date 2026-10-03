package com.petshop.backend.dto.response.common;

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
@Schema(description = "Average, count and the 5-bar star distribution")
public class ReviewSummaryResponse {
    private String targetType;
    private Long targetId;
    @Schema(example = "4.6")
    private BigDecimal average;
    private long count;
    @Schema(description = "Stars -> number of reviews", example = "{\"5\": 34, \"4\": 6, \"3\": 2, \"2\": 0, \"1\": 0}")
    private Map<Integer, Long> distribution;
    @Schema(description = "Stars -> share of reviews in percent", example = "{\"5\": 81, \"4\": 14, \"3\": 5, \"2\": 0, \"1\": 0}")
    private Map<Integer, Integer> percentages;
}
