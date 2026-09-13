package com.petshop.backend.dto.response.vendor;

import java.math.BigDecimal;

public class VendorFinanceStatsResponse {
    private BigDecimal totalRevenue;
    private BigDecimal availableBalance;
    private BigDecimal pendingPayouts;
    private Integer totalSalesCount;
    private Integer completedOrdersCount;

    public VendorFinanceStatsResponse() {}
    public VendorFinanceStatsResponse(BigDecimal totalRevenue, BigDecimal availableBalance, BigDecimal pendingPayouts, Integer totalSalesCount, Integer completedOrdersCount) {
        this.totalRevenue = totalRevenue;
        this.availableBalance = availableBalance;
        this.pendingPayouts = pendingPayouts;
        this.totalSalesCount = totalSalesCount;
        this.completedOrdersCount = completedOrdersCount;
    }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getAvailableBalance() { return availableBalance; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }

    public BigDecimal getPendingPayouts() { return pendingPayouts; }
    public void setPendingPayouts(BigDecimal pendingPayouts) { this.pendingPayouts = pendingPayouts; }

    public Integer getTotalSalesCount() { return totalSalesCount; }
    public void setTotalSalesCount(Integer totalSalesCount) { this.totalSalesCount = totalSalesCount; }

    public Integer getCompletedOrdersCount() { return completedOrdersCount; }
    public void setCompletedOrdersCount(Integer completedOrdersCount) { this.completedOrdersCount = completedOrdersCount; }
}