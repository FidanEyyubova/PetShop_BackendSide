package com.petshop.backend.controller.admin;

import com.petshop.backend.dto.request.content.AboutSectionRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.content.AboutSectionResponse;
import com.petshop.backend.service.content.AboutSectionService;
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
@RequestMapping("/api/v1/admin/about")
@Tag(name = "Admin Panel", description = "Unified Admin Panel management endpoints (Hero, Category, SubCategory, Product, Admin-Service, About, Blog, Contact, Messages, Settings)")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAboutSectionController {

    private final AboutSectionService aboutSectionService;

    public AdminAboutSectionController(AboutSectionService aboutSectionService) {
        this.aboutSectionService = aboutSectionService;
    }

    @PostMapping
    @Operation(summary = "About Section - Add content (title, subtitle, image)")
    public ResponseEntity<ApiResponse<AboutSectionResponse>> createAboutSection(@Valid @RequestBody AboutSectionRequest request) {
        AboutSectionResponse response = aboutSectionService.createAboutSection(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("About section created successfully", response));
    }

    @GetMapping
    @Operation(summary = "About Section - Get content")
    public ResponseEntity<ApiResponse<List<AboutSectionResponse>>> getAllAboutSections() {
        List<AboutSectionResponse> response = aboutSectionService.getAllAboutSections();
        return ResponseEntity.ok(ApiResponse.success("About sections retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "About Section - Get content by ID")
    public ResponseEntity<ApiResponse<AboutSectionResponse>> getAboutSectionById(@PathVariable Long id) {
        AboutSectionResponse response = aboutSectionService.getAboutSectionById(id);
        return ResponseEntity.ok(ApiResponse.success("About section retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "About Section - Update content by ID")
    public ResponseEntity<ApiResponse<AboutSectionResponse>> updateAboutSection(
            @PathVariable Long id,
            @Valid @RequestBody AboutSectionRequest request) {
        AboutSectionResponse response = aboutSectionService.updateAboutSection(id, request);
        return ResponseEntity.ok(ApiResponse.success("About section updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "About Section - Delete content by ID")
    public ResponseEntity<ApiResponse<Void>> deleteAboutSection(@PathVariable Long id) {
        aboutSectionService.deleteAboutSection(id);
        return ResponseEntity.ok(ApiResponse.success("About section deleted successfully", null));
    }
}