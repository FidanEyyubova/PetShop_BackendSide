package com.petshop.backend.controller.admin;

import com.petshop.backend.dto.request.content.SiteSettingsRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.content.SiteSettingsResponse;
import com.petshop.backend.service.content.SiteSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/settings")
@Tag(name = "Admin Panel", description = "Unified Admin Panel management endpoints (Hero, Category, SubCategory, Product, Admin-Service, About, Blog, Contact, Messages, Settings)")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSiteSettingsController {

    private final SiteSettingsService siteSettingsService;

    public AdminSiteSettingsController(SiteSettingsService siteSettingsService) {
        this.siteSettingsService = siteSettingsService;
    }

    @GetMapping
    @Operation(summary = "Settings - Get site settings (social media: instagram, tiktok, facebook, x, logo)")
    public ResponseEntity<ApiResponse<SiteSettingsResponse>> getSettings() {
        SiteSettingsResponse response = siteSettingsService.getSettings();
        return ResponseEntity.ok(ApiResponse.success("Site settings retrieved successfully", response));
    }

    @PostMapping
    @Operation(summary = "Settings - Save/Update site settings")
    public ResponseEntity<ApiResponse<SiteSettingsResponse>> saveSettings(@Valid @RequestBody SiteSettingsRequest request) {
        SiteSettingsResponse response = siteSettingsService.saveOrUpdateSettings(request);
        return ResponseEntity.ok(ApiResponse.success("Site settings saved successfully", response));
    }
}