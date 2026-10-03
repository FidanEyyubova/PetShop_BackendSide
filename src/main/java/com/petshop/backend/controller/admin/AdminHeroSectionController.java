package com.petshop.backend.controller.admin;

import com.petshop.backend.dto.request.content.HeroSectionRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.content.HeroSectionResponse;
import com.petshop.backend.service.content.HeroSectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/hero")
@Tag(name = "Admin - Content", description = "Site content, blog, contact messages and review moderation")
@PreAuthorize("hasRole('ADMIN')")
public class AdminHeroSectionController {

    private final HeroSectionService heroSectionService;

    public AdminHeroSectionController(HeroSectionService heroSectionService) {
        this.heroSectionService = heroSectionService;
    }

    @PostMapping
    @Operation(summary = "Create a hero banner", description = "Background image, title, subtitle, price and discount price.")
    public ResponseEntity<ApiResponse<HeroSectionResponse>> createHeroSection(@Valid @RequestBody HeroSectionRequest request) {
        HeroSectionResponse response = heroSectionService.createHeroSection(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Hero section created successfully", response));
    }

    @GetMapping
    @Operation(summary = "List hero banners", description = "All home-page hero banners, active and inactive.")
    public ResponseEntity<ApiResponse<List<HeroSectionResponse>>> getAllHeroSections() {
        List<HeroSectionResponse> response = heroSectionService.getAllHeroSections();
        return ResponseEntity.ok(ApiResponse.success("Hero sections retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a hero banner", description = "One banner.")
    public ResponseEntity<ApiResponse<HeroSectionResponse>> getHeroSectionById(@PathVariable Long id) {
        HeroSectionResponse response = heroSectionService.getHeroSectionById(id);
        return ResponseEntity.ok(ApiResponse.success("Hero section retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a hero banner", description = "Replaces the banner's fields; active=false hides it.")
    public ResponseEntity<ApiResponse<HeroSectionResponse>> updateHeroSection(
            @PathVariable Long id,
            @Valid @RequestBody HeroSectionRequest request) {
        HeroSectionResponse response = heroSectionService.updateHeroSection(id, request);
        return ResponseEntity.ok(ApiResponse.success("Hero section updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a hero banner", description = "Removes the banner.")
    public ResponseEntity<ApiResponse<Void>> deleteHeroSection(@PathVariable Long id) {
        heroSectionService.deleteHeroSection(id);
        return ResponseEntity.ok(ApiResponse.success("Hero section deleted successfully", null));
    }
}