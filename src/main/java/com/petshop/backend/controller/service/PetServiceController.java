package com.petshop.backend.controller.service;

import com.petshop.backend.dto.request.service.CreateServiceRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.service.ProviderProfileResponse;
import com.petshop.backend.dto.response.service.ServiceResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.service.PetServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/services")
@Tag(name = "Pet Service Controller", description = "Pet services catalog (Grooming, Veterinary, Training), provider profiles, and service management")
public class PetServiceController {

    private final PetServiceService petServiceService;

    public PetServiceController(PetServiceService petServiceService) {
        this.petServiceService = petServiceService;
    }

    @GetMapping
    @Operation(summary = "Get all pet services with filtering and pagination")
    public ResponseEntity<ApiResponse<PageResponse<ServiceResponse>>> getAllServices(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean emergency,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ServiceResponse> response = petServiceService.getAllServices(search, categoryId, emergency, pageable);
        return ResponseEntity.ok(
                ApiResponse.<PageResponse<ServiceResponse>>builder()
                        .success(true)
                        .message("Services fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pet service details by ID")
    public ResponseEntity<ApiResponse<ServiceResponse>> getServiceById(@PathVariable Long id) {
        ServiceResponse response = petServiceService.getServiceById(id);
        return ResponseEntity.ok(
                ApiResponse.<ServiceResponse>builder()
                        .success(true)
                        .message("Service details fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/providers/{providerId}")
    @Operation(summary = "Get service provider profile by provider ID")
    public ResponseEntity<ApiResponse<ProviderProfileResponse>> getProviderProfile(@PathVariable Long providerId) {
        ProviderProfileResponse response = petServiceService.getProviderProfile(providerId);
        return ResponseEntity.ok(
                ApiResponse.<ProviderProfileResponse>builder()
                        .success(true)
                        .message("Provider profile fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SERVICE_PROVIDER', 'ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create a new pet service (Provider or Admin)")
    public ResponseEntity<ApiResponse<ServiceResponse>> createService(
            @Valid @RequestBody CreateServiceRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ServiceResponse response = petServiceService.createService(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ServiceResponse>builder()
                        .success(true)
                        .message("Service created successfully")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SERVICE_PROVIDER', 'ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update an existing pet service (Provider or Admin)")
    public ResponseEntity<ApiResponse<ServiceResponse>> updateService(
            @PathVariable Long id,
            @Valid @RequestBody CreateServiceRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ServiceResponse response = petServiceService.updateService(id, request, currentUser);
        return ResponseEntity.ok(
                ApiResponse.<ServiceResponse>builder()
                        .success(true)
                        .message("Service updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SERVICE_PROVIDER', 'ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete a pet service (Provider or Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteService(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        petServiceService.deleteService(id, currentUser);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Service deleted successfully")
                        .data(null)
                        .build()
        );
    }
}