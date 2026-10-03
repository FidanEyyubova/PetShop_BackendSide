package com.petshop.backend.controller.admin;

import com.petshop.backend.dto.request.content.AboutSectionRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.content.AboutSectionResponse;
import com.petshop.backend.service.content.AboutSectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/about")
@Tag(name = "Admin - Content", description = "Site content, blog, contact messages and review moderation")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAboutSectionController {

    private final AboutSectionService aboutSectionService;

    public AdminAboutSectionController(AboutSectionService aboutSectionService) {
        this.aboutSectionService = aboutSectionService;
    }

    @PostMapping
    @Operation(summary = "Create an about section", description = "Title, subtitle, image and description.")
    public ResponseEntity<ApiResponse<AboutSectionResponse>> createAboutSection(@Valid @RequestBody AboutSectionRequest request) {
        AboutSectionResponse response = aboutSectionService.createAboutSection(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("About section created successfully", response));
    }

    @GetMapping
    @Operation(summary = "List about sections", description = "Content blocks of the About section.")
    public ResponseEntity<ApiResponse<List<AboutSectionResponse>>> getAllAboutSections() {
        List<AboutSectionResponse> response = aboutSectionService.getAllAboutSections();
        return ResponseEntity.ok(ApiResponse.success("About sections retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an about section", description = "One content block.")
    public ResponseEntity<ApiResponse<AboutSectionResponse>> getAboutSectionById(@PathVariable Long id) {
        AboutSectionResponse response = aboutSectionService.getAboutSectionById(id);
        return ResponseEntity.ok(ApiResponse.success("About section retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an about section", description = "Replaces the block's fields.")
    public ResponseEntity<ApiResponse<AboutSectionResponse>> updateAboutSection(
            @PathVariable Long id,
            @Valid @RequestBody AboutSectionRequest request) {
        AboutSectionResponse response = aboutSectionService.updateAboutSection(id, request);
        return ResponseEntity.ok(ApiResponse.success("About section updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an about section", description = "Removes the block.")
    public ResponseEntity<ApiResponse<Void>> deleteAboutSection(@PathVariable Long id) {
        aboutSectionService.deleteAboutSection(id);
        return ResponseEntity.ok(ApiResponse.success("About section deleted successfully", null));
    }
}