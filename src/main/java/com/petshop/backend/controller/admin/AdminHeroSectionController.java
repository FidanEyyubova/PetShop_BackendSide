package com.petshop.backend.controller.admin;

import com.petshop.backend.dto.request.content.HeroSectionRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.content.HeroSectionResponse;
import com.petshop.backend.service.content.HeroSectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/hero")
@Tag(name = "Admin Panel", description = "Unified Admin Panel management endpoints (Hero, Category, SubCategory, Product, Admin-Service, About, Blog, Contact, Messages, Settings)")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AdminHeroSectionController {

    private final HeroSectionService heroSectionService;

    public AdminHeroSectionController(HeroSectionService heroSectionService) {
        this.heroSectionService = heroSectionService;
    }

    @PostMapping
    @Operation(summary = "Hero Section - Add banner (bg image, title, subtitle, price, discount price)")
    public ResponseEntity<ApiResponse<HeroSectionResponse>> createHeroSection(@Valid @RequestBody HeroSectionRequest request) {
        HeroSectionResponse response = heroSectionService.createHeroSection(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Hero section created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Hero Section - Get all banners")
    public ResponseEntity<ApiResponse<List<HeroSectionResponse>>> getAllHeroSections() {
        List<HeroSectionResponse> response = heroSectionService.getAllHeroSections();
        return ResponseEntity.ok(ApiResponse.success("Hero sections retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Hero Section - Get banner by ID")
    public ResponseEntity<ApiResponse<HeroSectionResponse>> getHeroSectionById(@PathVariable Long id) {
        HeroSectionResponse response = heroSectionService.getHeroSectionById(id);
        return ResponseEntity.ok(ApiResponse.success("Hero section retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Hero Section - Update banner by ID")
    public ResponseEntity<ApiResponse<HeroSectionResponse>> updateHeroSection(
            @PathVariable Long id,
            @Valid @RequestBody HeroSectionRequest request) {
        HeroSectionResponse response = heroSectionService.updateHeroSection(id, request);
        return ResponseEntity.ok(ApiResponse.success("Hero section updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hero Section - Delete banner by ID")
    public ResponseEntity<ApiResponse<Void>> deleteHeroSection(@PathVariable Long id) {
        heroSectionService.deleteHeroSection(id);
        return ResponseEntity.ok(ApiResponse.success("Hero section deleted successfully", null));
    }
}