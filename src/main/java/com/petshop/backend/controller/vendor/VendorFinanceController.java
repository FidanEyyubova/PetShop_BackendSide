package com.petshop.backend.controller.vendor;

import com.petshop.backend.common.Roles;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vendor/finance")
@PreAuthorize("hasAnyRole('" + Roles.VENDOR + "','" + Roles.ADMIN + "')")
@Tag(name = "Vendor - Finance", description = "Balance, commission, payouts and bank details")
public class VendorFinanceController {

    private final VendorFinanceService financeService;

    public VendorFinanceController(VendorFinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/stats")
    @Operation(summary = "Get my finance summary", description = "Revenue of delivered orders, commission, net earnings, available and pending balance, total withdrawn.")
    public ResponseEntity<ApiResponse<VendorFinanceStatsResponse>> stats(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Finance stats fetched", financeService.getFinanceStats(currentUser)));
    }

    @GetMapping("/payouts")
    @Operation(summary = "List my payouts", description = "Withdrawal history; the IBAN is masked.")
    public ResponseEntity<ApiResponse<List<VendorPayoutResponse>>> payouts(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Payouts fetched", financeService.getPayoutHistory(currentUser)));
    }

    @PostMapping("/payouts")
    @Operation(summary = "Request a payout", description = "The amount cannot exceed the available balance. Requires an approved account.")
    public ResponseEntity<ApiResponse<VendorPayoutResponse>> requestPayout(@Valid @RequestBody CreatePayoutRequest request,
                                                                           @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payout requested", financeService.createPayoutRequest(request, currentUser)));
    }

    @GetMapping("/bank-details")
    @Operation(summary = "Get my bank details", description = "IBAN, bank name, account holder and VÖEN.")
    public ResponseEntity<ApiResponse<VendorBankDetailsResponse>> bankDetails(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Bank details fetched", financeService.getBankDetails(currentUser)));
    }

    @PutMapping("/bank-details")
    @Operation(summary = "Save my bank details", description = "IBAN in Azerbaijani format; VÖEN is 10 digits.")
    public ResponseEntity<ApiResponse<VendorBankDetailsResponse>> updateBankDetails(@Valid @RequestBody VendorBankDetailsRequest request,
                                                                                    @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Bank details saved", financeService.updateBankDetails(request, currentUser)));
    }
}
