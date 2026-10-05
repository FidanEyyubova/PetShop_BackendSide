package com.petshop.backend.service.vendor;

import com.petshop.backend.dto.request.vendor.CreatePayoutRequest;
import com.petshop.backend.dto.request.vendor.VendorBankDetailsRequest;
import com.petshop.backend.dto.response.vendor.VendorBankDetailsResponse;
import com.petshop.backend.dto.response.vendor.VendorFinanceStatsResponse;
import com.petshop.backend.dto.response.vendor.VendorPayoutResponse;
import com.petshop.backend.security.UserPrincipal;

import java.util.List;

public interface VendorFinanceService {
    VendorFinanceStatsResponse getFinanceStats(UserPrincipal currentUser);
    List<VendorPayoutResponse> getPayoutHistory(UserPrincipal currentUser);
    VendorPayoutResponse createPayoutRequest(CreatePayoutRequest request, UserPrincipal currentUser);
    VendorBankDetailsResponse getBankDetails(UserPrincipal currentUser);
    /** Bank details of any vendor (admin review); empty fields when the vendor has saved none. */
    VendorBankDetailsResponse getBankDetailsOf(Long vendorId);
    VendorBankDetailsResponse updateBankDetails(VendorBankDetailsRequest request, UserPrincipal currentUser);
}
