package com.petshop.backend.controller.vendor;

import com.petshop.backend.common.Roles;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.common.ReviewResponse;
import com.petshop.backend.dto.response.vendor.SalesSeriesResponse;
import com.petshop.backend.dto.response.vendor.VendorDashboardResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.content.ReviewService;
import com.petshop.backend.service.vendor.VendorDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vendor")
@PreAuthorize("hasAnyRole('" + Roles.VENDOR + "','" + Roles.ADMIN + "')")
@Tag(name = "Vendor - Dashboard", description = "Overview KPIs, sales chart and reviews of the vendor's products/store")
public class VendorDashboardController {

    private final VendorDashboardService dashboardService;
    private final ReviewService reviewService;

    public VendorDashboardController(VendorDashboardService dashboardService, ReviewService reviewService) {
        this.dashboardService = dashboardService;
        this.reviewService = reviewService;
    }

    @GetMapping("/dashboard/summary")
    @Operation(summary = "Get my dashboard", description = "Revenue this month and change vs last month, active and new orders, units sold, products and low stock.")
    public ResponseEntity<ApiResponse<VendorDashboardResponse>> summary(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Dashboard fetched", dashboardService.summary(currentUser)));
    }

    @GetMapping("/dashboard/sales")
    @Operation(summary = "Get the sales chart", description = "Revenue and order count per day (weekly) or per month (monthly).")
    public ResponseEntity<ApiResponse<SalesSeriesResponse>> sales(
            @Parameter(description = "weekly (last 7 days) or monthly (this year)") @RequestParam(defaultValue = "monthly") String range,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Sales fetched", dashboardService.sales(range, currentUser)));
    }

    @GetMapping("/reviews")
    @Operation(summary = "List reviews of my store", description = "Reviews of my products and store; reply via POST /api/v1/reviews/{id}/reply.")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> reviews(@RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size,
                                                                             @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Reviews fetched", reviewService.getVendorReviews(currentUser, page, size)));
    }
}
