package com.petshop.backend.controller.content;

import com.petshop.backend.dto.request.common.AddReviewRequest;
import com.petshop.backend.dto.request.common.ReviewReplyRequest;
import com.petshop.backend.dto.request.common.UpdateReviewRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.common.ReviewResponse;
import com.petshop.backend.dto.response.common.ReviewSummaryResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.content.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Reviews", description = "Ratings and reviews for products, stores and service providers")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    @Operation(summary = "List reviews", description = "Reviews of one product, store or service provider; sort by newest, highest or lowest.")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> list(
            @Parameter(description = "PRODUCT, STORE or SERVICE_PROVIDER", required = true) @RequestParam String targetType,
            @Parameter(description = "Product id, store id or provider user id", required = true) @RequestParam Long targetId,
            @Parameter(description = "newest (default), highest, lowest") @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success("Reviews fetched",
                reviewService.getReviewsByTarget(targetType, targetId, sort, page, size)));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get the rating summary", description = "Average rating, review count and the 5-star distribution.")
    public ResponseEntity<ApiResponse<ReviewSummaryResponse>> summary(@RequestParam String targetType,
                                                                      @RequestParam Long targetId) {
        return ResponseEntity.ok(ApiResponse.success("Review summary fetched", reviewService.getSummary(targetType, targetId)));
    }

    @GetMapping("/my")
    @Operation(summary = "List my reviews", description = "Includes the reviewed item's name and store.")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> my(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size,
                                                                       @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Reviews fetched", reviewService.getMyReviews(currentUser, page, size)));
    }

    @PostMapping
    @Operation(summary = "Write a review",
            description = "One review per item. Products and stores require a delivered order (403 REVIEW_REQUIRES_PURCHASE); "
                    + "duplicates return 409 DUPLICATE_REVIEW. The item's rating and review count are updated.")
    public ResponseEntity<ApiResponse<ReviewResponse>> add(@Valid @RequestBody AddReviewRequest request,
                                                           @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review added", reviewService.addReview(request, currentUser)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update my review", description = "Fields that are not sent stay unchanged; the rating is recalculated.")
    public ResponseEntity<ApiResponse<ReviewResponse>> update(@PathVariable Long id,
                                                              @Valid @RequestBody UpdateReviewRequest request,
                                                              @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Review updated", reviewService.updateReview(id, request, currentUser)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete my review", description = "Authors delete their own reviews; admins any review.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        reviewService.deleteReview(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Review deleted"));
    }

    @RequestMapping(value = "/{id}/reply", method = {RequestMethod.POST, RequestMethod.PUT})
    @Operation(summary = "Reply to a review", description = "Only the owner of the reviewed product, store or center (or an admin).")
    public ResponseEntity<ApiResponse<ReviewResponse>> reply(@PathVariable Long id,
                                                             @Valid @RequestBody ReviewReplyRequest request,
                                                             @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Reply saved", reviewService.replyToReview(id, request, currentUser)));
    }
}
