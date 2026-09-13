package com.petshop.backend.controller.admin;

import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.common.ReviewResponse;
import com.petshop.backend.service.content.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/reviews")
@Tag(name = "Admin Panel", description = "Unified Admin Panel management endpoints (Hero, Category, SubCategory, Product, Admin-Service, About, Blog, Contact, Messages, Reviews, Settings)")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    @Operation(summary = "Get all customer reviews across products and services (Admin)")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getAllReviews(
            @RequestParam(required = false) String targetType,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<ReviewResponse> response = reviewService.getAllReviewsForAdmin(targetType, pageable);
        return ResponseEntity.ok(ApiResponse.success("All reviews fetched successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete any customer review by ID (Admin Moderation)")
    public ResponseEntity<ApiResponse<Void>> deleteReviewByAdmin(@PathVariable Long id) {
        reviewService.deleteReviewByAdmin(id);
        return ResponseEntity.ok(ApiResponse.success("Review deleted by Admin successfully", null));
    }
}