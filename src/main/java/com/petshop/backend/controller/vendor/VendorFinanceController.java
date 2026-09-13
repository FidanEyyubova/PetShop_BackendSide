package com.petshop.backend.controller.vendor;

import com.petshop.backend.dto.request.vendor.CreatePayoutRequest;
import com.petshop.backend.dto.request.vendor.VendorBankDetailsRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.vendor.VendorBankDetailsResponse;
import com.petshop.backend.dto.response.vendor.VendorFinanceStatsResponse;
import com.petshop.backend.dto.response.vendor.VendorPayoutResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.vendor.VendorFinanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vendor/finance")
@Tag(name = "Vendor Finance Controller", description = "Vendor financial statistics, balance, payout withdrawals, and bank details management")
@PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
public class VendorFinanceController {

    private final VendorFinanceService vendorFinanceService;

    public VendorFinanceController(VendorFinanceService vendorFinanceService) {
        this.vendorFinanceService = vendorFinanceService;
    }

    @GetMapping("/stats")
    @Operation(summary = "Get vendor revenue, balance, and payout statistics")
    public ResponseEntity<ApiResponse<VendorFinanceStatsResponse>> getFinanceStats(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        VendorFinanceStatsResponse response = vendorFinanceService.getFinanceStats(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Finance statistics fetched successfully", response));
    }

    @GetMapping("/payouts")
    @Operation(summary = "Get vendor payout withdrawal history")
    public ResponseEntity<ApiResponse<List<VendorPayoutResponse>>> getPayoutHistory(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        List<VendorPayoutResponse> response = vendorFinanceService.getPayoutHistory(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Payout history fetched successfully", response));
    }

    @PostMapping("/payouts")
    @Operation(summary = "Request new payout withdrawal (amount, iban, bankName, accountHolder)")
    public ResponseEntity<ApiResponse<VendorPayoutResponse>> createPayoutRequest(
            @Valid @RequestBody CreatePayoutRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        VendorPayoutResponse response = vendorFinanceService.createPayoutRequest(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Payout request submitted successfully", response));
    }

    @GetMapping("/bank-details")
    @Operation(summary = "View vendor bank account details")
    public ResponseEntity<ApiResponse<VendorBankDetailsResponse>> getBankDetails(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        VendorBankDetailsResponse response = vendorFinanceService.getBankDetails(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Bank details fetched successfully", response));
    }

    @PutMapping("/bank-details")
    @Operation(summary = "Update vendor bank account details")
    public ResponseEntity<ApiResponse<VendorBankDetailsResponse>> updateBankDetails(
            @Valid @RequestBody VendorBankDetailsRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        VendorBankDetailsResponse response = vendorFinanceService.updateBankDetails(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Bank details updated successfully", response));
    }
}