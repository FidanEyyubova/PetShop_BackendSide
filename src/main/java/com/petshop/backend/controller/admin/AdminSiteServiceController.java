package com.petshop.backend.controller.admin;

import com.petshop.backend.dto.request.content.AdminServiceRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.content.AdminServiceResponse;
import com.petshop.backend.service.content.AdminServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/site-services")
@Tag(name = "Admin - Content", description = "Site content, blog, contact messages and review moderation")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSiteServiceController {

    private final AdminServiceService adminServiceService;

    public AdminSiteServiceController(AdminServiceService adminServiceService) {
        this.adminServiceService = adminServiceService;
    }

    @PostMapping
    @Operation(summary = "Create a home-page service item", description = "Title, subtitle and image.")
    public ResponseEntity<ApiResponse<AdminServiceResponse>> createSiteService(@Valid @RequestBody AdminServiceRequest request) {
        AdminServiceResponse response = adminServiceService.createSiteService(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Site service created successfully", response));
    }

    @GetMapping
    @Operation(summary = "List home-page service items", description = "Items of the \"Our services\" banner, active and inactive.")
    public ResponseEntity<ApiResponse<List<AdminServiceResponse>>> getAllSiteServices() {
        List<AdminServiceResponse> response = adminServiceService.getAllSiteServices();
        return ResponseEntity.ok(ApiResponse.success("Site services retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a home-page service item", description = "One item.")
    public ResponseEntity<ApiResponse<AdminServiceResponse>> getSiteServiceById(@PathVariable Long id) {
        AdminServiceResponse response = adminServiceService.getSiteServiceById(id);
        return ResponseEntity.ok(ApiResponse.success("Site service retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a home-page service item", description = "Replaces the item's fields; active=false hides it.")
    public ResponseEntity<ApiResponse<AdminServiceResponse>> updateSiteService(
            @PathVariable Long id,
            @Valid @RequestBody AdminServiceRequest request) {
        AdminServiceResponse response = adminServiceService.updateSiteService(id, request);
        return ResponseEntity.ok(ApiResponse.success("Site service updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a home-page service item", description = "Removes the item.")
    public ResponseEntity<ApiResponse<Void>> deleteSiteService(@PathVariable Long id) {
        adminServiceService.deleteSiteService(id);
        return ResponseEntity.ok(ApiResponse.success("Site service deleted successfully", null));
    }
}