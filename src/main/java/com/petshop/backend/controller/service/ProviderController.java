package com.petshop.backend.controller.service;

import com.petshop.backend.dto.request.service.*;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.service.*;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.service.ProviderManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pet-services/provider")
@Tag(name = "Provider Management Controller", description = "Service provider profile, team members, weekly schedules, and gallery showcase management")
public class ProviderController {

    private final ProviderManagementService providerManagementService;

    public ProviderController(ProviderManagementService providerManagementService) {
        this.providerManagementService = providerManagementService;
    }

    @PutMapping({"/profile", "/me"})
    @PreAuthorize("hasAnyRole('SERVICE_PROVIDER', 'ADMIN')")
    @Operation(summary = "Update service provider profile and contact information")
    public ResponseEntity<ApiResponse<ProviderProfileResponse>> updateProviderProfile(
            @Valid @RequestBody UpdateProviderProfileRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ProviderProfileResponse response = providerManagementService.updateProviderProfile(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Provider profile updated successfully", response));
    }

    @PostMapping("/team-members")
    @PreAuthorize("hasAnyRole('SERVICE_PROVIDER', 'ADMIN')")
    @Operation(summary = "Add a new team member")
    public ResponseEntity<ApiResponse<TeamMemberResponse>> addTeamMember(
            @Valid @RequestBody AddTeamMemberRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        TeamMemberResponse response = providerManagementService.addTeamMember(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Team member added successfully", response));
    }

    @PutMapping("/team-members/{id}")
    @PreAuthorize("hasAnyRole('SERVICE_PROVIDER', 'ADMIN')")
    @Operation(summary = "Update team member details")
    public ResponseEntity<ApiResponse<TeamMemberResponse>> updateTeamMember(
            @PathVariable Long id,
            @Valid @RequestBody AddTeamMemberRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        TeamMemberResponse response = providerManagementService.updateTeamMember(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Team member updated successfully", response));
    }

    @DeleteMapping("/team-members/{id}")
    @PreAuthorize("hasAnyRole('SERVICE_PROVIDER', 'ADMIN')")
    @Operation(summary = "Delete team member by ID")
    public ResponseEntity<ApiResponse<Void>> deleteTeamMember(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        providerManagementService.deleteTeamMember(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Team member deleted successfully"));
    }

    @PutMapping("/schedules")
    @PreAuthorize("hasAnyRole('SERVICE_PROVIDER', 'ADMIN')")
    @Operation(summary = "Update weekly work schedule list")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> updateSchedules(
            @Valid @RequestBody List<ScheduleRequest> requests,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        List<ScheduleResponse> response = providerManagementService.updateSchedules(requests, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Weekly schedules updated successfully", response));
    }

    @GetMapping("/{providerId}/gallery")
    @Operation(summary = "Get service provider gallery showcase images")
    public ResponseEntity<ApiResponse<List<ProviderGalleryResponse>>> getProviderGallery(
            @PathVariable Long providerId
    ) {
        List<ProviderGalleryResponse> response = providerManagementService.getProviderGallery(providerId);
        return ResponseEntity.ok(ApiResponse.success("Provider gallery fetched successfully", response));
    }

    @PostMapping("/gallery")
    @PreAuthorize("hasAnyRole('SERVICE_PROVIDER', 'ADMIN')")
    @Operation(summary = "Add new image to provider gallery showcase")
    public ResponseEntity<ApiResponse<ProviderGalleryResponse>> addGalleryImage(
            @Valid @RequestBody AddGalleryImageRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ProviderGalleryResponse response = providerManagementService.addGalleryImage(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Gallery image added successfully", response));
    }

    @DeleteMapping("/gallery/{id}")
    @PreAuthorize("hasAnyRole('SERVICE_PROVIDER', 'ADMIN')")
    @Operation(summary = "Delete image from provider gallery showcase")
    public ResponseEntity<ApiResponse<Void>> deleteGalleryImage(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        providerManagementService.deleteGalleryImage(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Gallery image deleted successfully"));
    }
}