package com.petshop.backend.controller.admin;

import com.petshop.backend.common.Roles;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.common.ReviewResponse;
import com.petshop.backend.service.content.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/reviews")
@PreAuthorize("hasRole('" + Roles.ADMIN + "')")
@Tag(name = "Admin - Content", description = "Site content, blog, contact messages and review moderation")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    @Operation(summary = "List all reviews", description = "Every review for moderation; filter by target type.")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> list(
            @Parameter(description = "PRODUCT, STORE or SERVICE_PROVIDER") @RequestParam(required = false) String targetType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success("Reviews fetched", reviewService.getAllReviewsForAdmin(targetType, page, size)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review", description = "Removes any review; the reviewed item's rating is recalculated.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        reviewService.deleteReviewByAdmin(id);
        return ResponseEntity.ok(ApiResponse.success("Review deleted"));
    }
}
