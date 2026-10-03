package com.petshop.backend.dto.response.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderStatsResponse {
    private long pageViews;
    private long contactClicks;
    private long wishlistAdds;
    private BigDecimal rating;
    private Integer reviewsCount;
    private Integer profileCompleteness;
}
