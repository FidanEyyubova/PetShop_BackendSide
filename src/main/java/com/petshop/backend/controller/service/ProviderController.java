package com.petshop.backend.controller.service;

import com.petshop.backend.common.Roles;
import com.petshop.backend.dto.request.service.*;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.service.*;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.dto.request.vendor.VendorBankDetailsRequest;
import com.petshop.backend.dto.response.vendor.VendorBankDetailsResponse;
import com.petshop.backend.service.service.ProviderManagementService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pet-services/provider")
@Tag(name = "Provider Dashboard", description = "Service provider's own center: profile, team, price list, schedule, gallery, metrics")
public class ProviderController {

    private static final String PROVIDER = "hasAnyRole('" + Roles.SERVICE_PROVIDER + "','" + Roles.ADMIN + "')";

    private final ProviderManagementService providerService;
    private final VendorFinanceService financeService;

    public ProviderController(ProviderManagementService providerService, VendorFinanceService financeService) {
        this.providerService = providerService;
        this.financeService = financeService;
    }

    // ---- profile -------------------------------------------------------------------------------

    @GetMapping({"/me", "/profile"})
    @PreAuthorize(PROVIDER)
    @Operation(summary = "Get my center", description = "Full profile including inactive services, all team members, account status and profile completeness.")
    public ResponseEntity<ApiResponse<ProviderProfileResponse>> me(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Profile fetched", providerService.getMyProfile(currentUser)));
    }

    @PutMapping({"/me", "/profile"})
    @PreAuthorize(PROVIDER)
    @Operation(summary = "Update my center", description = "Partial update: fields that are not sent stay unchanged.")
    public ResponseEntity<ApiResponse<ProviderProfileResponse>> update(@Valid @RequestBody UpdateProviderProfileRequest request,
                                                                       @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", providerService.updateProviderProfile(request, currentUser)));
    }

    @GetMapping("/me/stats")
    @PreAuthorize(PROVIDER)
    @Operation(summary = "Get my metrics", description = "Page views, contact clicks, wishlist adds, rating, reviews count and profile completeness.")
    public ResponseEntity<ApiResponse<ProviderStatsResponse>> stats(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Stats fetched", providerService.getStats(currentUser)));
    }

    // ---- bank & legal details (same store as sellers; admins review them before approval) -------

    @GetMapping("/bank-details")
    @PreAuthorize(PROVIDER)
    @Operation(summary = "Get my bank details", description = "VÖEN, IBAN, bank name and account holder; empty fields when nothing is saved.")
    public ResponseEntity<ApiResponse<VendorBankDetailsResponse>> bankDetails(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Bank details fetched", financeService.getBankDetails(currentUser)));
    }

    @PutMapping("/bank-details")
    @PreAuthorize(PROVIDER)
    @Operation(summary = "Save my bank details", description = "Required for account approval: VÖEN, IBAN, bank name and account holder.")
    public ResponseEntity<ApiResponse<VendorBankDetailsResponse>> updateBankDetails(@Valid @RequestBody VendorBankDetailsRequest request,
                                                                                   @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Bank details saved", financeService.updateBankDetails(request, currentUser)));
    }

    // ---- team ----------------------------------------------------------------------------------

    @GetMapping("/team-members")
    @PreAuthorize(PROVIDER)
    @Operation(summary = "List my team", description = "All team members.")
    public ResponseEntity<ApiResponse<List<TeamMemberResponse>>> team(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Team fetched", providerService.getTeam(currentUser)));
    }

    @PostMapping("/team-members")
    @PreAuthorize(PROVIDER)
    @Operation(summary = "Add a team member", description = "featured=true shows the member publicly (at most 4; otherwise 409 FEATURED_LIMIT).")
    public ResponseEntity<ApiResponse<TeamMemberResponse>> addMember(@Valid @RequestBody AddTeamMemberRequest request,
                                                                     @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Team member added", providerService.addTeamMember(request, currentUser)));
    }

    @PutMapping("/team-members/{id}")
    @PreAuthorize(PROVIDER)
    @Operation(summary = "Update a team member", description = "Replaces the member's fields.")
    public ResponseEntity<ApiResponse<TeamMemberResponse>> updateMember(@PathVariable Long id,
                                                                        @Valid @RequestBody AddTeamMemberRequest request,
                                                                        @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Team member updated", providerService.updateTeamMember(id, request, currentUser)));
    }

    @PatchMapping("/team-members/{id}/featured")
    @PreAuthorize(PROVIDER)
    @Operation(summary = "Feature or unfeature a team member", description = "Body: {\"featured\": true}. At most 4 featured members.")
    public ResponseEntity<ApiResponse<TeamMemberResponse>> feature(@PathVariable Long id,
                                                                   @RequestBody Map<String, Boolean> body,
                                                                   @AuthenticationPrincipal UserPrincipal currentUser) {
        boolean featured = Boolean.TRUE.equals(body.get("featured"));
        return ResponseEntity.ok(ApiResponse.success("Team member updated", providerService.setFeatured(id, featured, currentUser)));
    }

    @DeleteMapping("/team-members/{id}")
    @PreAuthorize(PROVIDER)
    @Operation(summary = "Delete a team member", description = "Removes the member.")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        providerService.deleteTeamMember(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Team member removed"));
    }

    // ---- services ------------------------------------------------------------------------------

    @GetMapping("/services")
    @PreAuthorize(PROVIDER)
    @Operation(summary = "List my services", description = "My price list, active and inactive.")
    public ResponseEntity<ApiResponse<List<ServiceResponse>>> services(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Services fetched", providerService.getMyServices(currentUser)));
    }

    @PostMapping("/services")
    @PreAuthorize(PROVIDER)
    @Operation(summary = "Create a service", description = "Price-list line; priceMax must be greater than or equal to priceMin. "
            + "While the account is PENDING_APPROVAL the service is saved hidden (active=false).")
    public ResponseEntity<ApiResponse<ServiceResponse>> createService(@Valid @RequestBody CreateServiceRequest request,
                                                                      @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Service created", providerService.createService(request, currentUser)));
    }

    @PutMapping("/services/{id}")
    @PreAuthorize(PROVIDER)
    @Operation(summary = "Update a service", description = "Replaces the line's fields. Stays hidden while the account is PENDING_APPROVAL.")
    public ResponseEntity<ApiResponse<ServiceResponse>> updateService(@PathVariable Long id,
                                                                      @Valid @RequestBody CreateServiceRequest request,
                                                                      @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Service updated", providerService.updateService(id, request, currentUser)));
    }

    @PatchMapping("/services/{id}/status")
    @PreAuthorize(PROVIDER)
    @Operation(summary = "Activate or deactivate a service", description = "Body: {\"active\": false}. Inactive services are hidden publicly. "
            + "Activating requires an approved account (403 ACCOUNT_PENDING_APPROVAL otherwise).")
    public ResponseEntity<ApiResponse<ServiceResponse>> serviceStatus(@PathVariable Long id,
                                                                      @RequestBody Map<String, Boolean> body,
                                                                      @AuthenticationPrincipal UserPrincipal currentUser) {
        boolean active = !Boolean.FALSE.equals(body.get("active"));
        return ResponseEntity.ok(ApiResponse.success("Service updated", providerService.setServiceActive(id, active, currentUser)));
    }

    @DeleteMapping("/services/{id}")
    @PreAuthorize(PROVIDER)
    @Operation(summary = "Delete a service", description = "Removes the line.")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        providerService.deleteService(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Service deleted"));
    }

    // ---- schedule ------------------------------------------------------------------------------

    @PutMapping("/schedules")
    @PreAuthorize(PROVIDER)
    @Operation(summary = "Save the weekly schedule", description = "Replaces the whole week. Times use HH:mm; isActive=false or a missing day means closed.")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> schedules(@Valid @RequestBody List<@Valid ScheduleRequest> requests,
                                                                         @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Schedule saved", providerService.updateSchedules(requests, currentUser)));
    }

    // ---- gallery -------------------------------------------------------------------------------

    @GetMapping("/{providerId}/gallery")
    @Operation(summary = "Get a provider's gallery", description = "Public gallery images, newest first.")
    public ResponseEntity<ApiResponse<List<ProviderGalleryResponse>>> gallery(@PathVariable Long providerId) {
        return ResponseEntity.ok(ApiResponse.success("Gallery fetched", providerService.getProviderGallery(providerId)));
    }

    @PostMapping("/gallery")
    @PreAuthorize(PROVIDER)
    @Operation(summary = "Add a gallery image", description = "Upload via POST /api/v1/files first, then send the URL.")
    public ResponseEntity<ApiResponse<ProviderGalleryResponse>> addImage(@Valid @RequestBody AddGalleryImageRequest request,
                                                                         @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Image added", providerService.addGalleryImage(request, currentUser)));
    }

    @DeleteMapping("/gallery/{id}")
    @PreAuthorize(PROVIDER)
    @Operation(summary = "Delete a gallery image", description = "Owner or admin only.")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        providerService.deleteGalleryImage(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Image deleted"));
    }
}
