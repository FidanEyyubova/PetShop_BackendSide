package com.petshop.backend.controller.content;

import com.petshop.backend.dto.request.common.AddReviewRequest;
import com.petshop.backend.dto.request.common.ReviewReplyRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.common.ReviewResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.content.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Review Controller", description = "Product and pet service reviews, star ratings, and user/provider review management")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    @Operation(summary = "Get all reviews by targetType (PRODUCT, SERVICE) and targetId")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getReviewsByTarget(
            @RequestParam String targetType,
            @RequestParam Long targetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<ReviewResponse> response = reviewService.getReviewsByTarget(targetType, targetId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Reviews fetched successfully", response));
    }

    @GetMapping("/my")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get currently authenticated user's submitted reviews")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<ReviewResponse> response = reviewService.getMyReviews(currentUser, pageable);
        return ResponseEntity.ok(ApiResponse.success("My reviews fetched successfully", response));
    }

    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Add a new review for a product or pet service")
    public ResponseEntity<ApiResponse<ReviewResponse>> addReview(
            @Valid @RequestBody AddReviewRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        ReviewResponse response = reviewService.addReview(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Review submitted successfully", response));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update user's own review by ID")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody AddReviewRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        ReviewResponse response = reviewService.updateReview(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Review updated successfully", response));
    }

    @PostMapping("/{id}/reply")
    @PreAuthorize("hasAnyRole('VENDOR', 'SERVICE_PROVIDER', 'ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Add a reply to a review (Vendor or Service Provider)")
    public ResponseEntity<ApiResponse<ReviewResponse>> replyToReviewPost(
            @PathVariable Long id,
            @Valid @RequestBody ReviewReplyRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        ReviewResponse response = reviewService.replyToReview(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Reply added to review successfully", response));
    }

    @PutMapping("/{id}/reply")
    @PreAuthorize("hasAnyRole('VENDOR', 'SERVICE_PROVIDER', 'ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update a reply on a review (Vendor or Service Provider)")
    public ResponseEntity<ApiResponse<ReviewResponse>> replyToReviewPut(
            @PathVariable Long id,
            @Valid @RequestBody ReviewReplyRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        ReviewResponse response = reviewService.replyToReview(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Reply updated on review successfully", response));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete user's own review by ID")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        reviewService.deleteReview(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully", null));
    }
}