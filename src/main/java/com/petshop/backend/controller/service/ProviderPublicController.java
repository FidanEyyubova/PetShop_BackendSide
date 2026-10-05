package com.petshop.backend.controller.service;

import com.petshop.backend.dto.request.service.ProviderEventRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.service.ProviderProfileResponse;
import com.petshop.backend.dto.response.service.ProviderSummaryResponse;
import com.petshop.backend.dto.response.service.ServiceCategoryResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.service.ProviderManagementService;
import com.petshop.backend.service.service.ProviderQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Service Providers", description = "Public /services pages: categories, provider listing and provider page")
public class ProviderPublicController {

    private final ProviderQueryService queryService;
    private final ProviderManagementService managementService;

    public ProviderPublicController(ProviderQueryService queryService, ProviderManagementService managementService) {
        this.queryService = queryService;
        this.managementService = managementService;
    }

    @GetMapping("/service-categories")
    @Operation(summary = "List service categories", description = "Grooming, veterinary and training with provider counts.")
    public ResponseEntity<ApiResponse<List<ServiceCategoryResponse>>> categories() {
        return ResponseEntity.ok(ApiResponse.success("Service categories fetched", queryService.serviceCategories()));
    }

    @GetMapping("/providers")
    @Operation(summary = "List service providers", description = "Approved providers, best rated first, with price range, open-now flag and today's hours.")
    public ResponseEntity<ApiResponse<PageResponse<ProviderSummaryResponse>>> list(
            @Parameter(description = "grooming, veterinary or training (frontend ?service=)") @RequestParam(required = false) String serviceType,
            @Parameter(description = "Alias of serviceType") @RequestParam(required = false) String service,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String q,
            @Parameter(description = "Only providers open right now (filters the current page)") @RequestParam(required = false) Boolean openNow,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(ApiResponse.success("Providers fetched",
                queryService.list(serviceType != null ? serviceType : service, city, q, openNow, page, size)));
    }

    @GetMapping("/providers/{id}")
    @Operation(summary = "Get a service provider", description = "Profile, price list, featured team, weekly schedule, gallery, amenities and rating. "
            + "Admins also get providers waiting for approval, with the private fields (email, account status).")
    public ResponseEntity<ApiResponse<ProviderProfileResponse>> get(@PathVariable Long id,
                                                                    @AuthenticationPrincipal UserPrincipal viewer) {
        boolean adminView = viewer != null && viewer.isAdmin();
        return ResponseEntity.ok(ApiResponse.success("Provider fetched", queryService.publicProfile(id, adminView)));
    }

    @PostMapping("/providers/{id}/events")
    @Operation(summary = "Record a visitor event", description = "Counts a page view, contact click or wishlist add for the provider's dashboard.")
    public ResponseEntity<ApiResponse<Void>> event(@PathVariable Long id, @Valid @RequestBody ProviderEventRequest request) {
        managementService.recordEvent(id, request);
        return ResponseEntity.ok(ApiResponse.success("Event recorded"));
    }
}
