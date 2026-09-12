package com.petshop.backend.controller.content;

import com.petshop.backend.dto.request.common.AddReviewRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.common.ReviewResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.content.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getReviews(
            @RequestParam String targetType,
            @RequestParam Long targetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ReviewResponse> response = reviewService.getReviewsByTarget(targetType, targetId, pageable);
        return ResponseEntity.ok(
                ApiResponse.<PageResponse<ReviewResponse>>builder()
                        .success(true)
                        .message("RÉ™ylÉ™r siyahÄ±sÄ±")
                        .data(response)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> addReview(
            @Valid @RequestBody AddReviewRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ReviewResponse response = reviewService.addReview(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ReviewResponse>builder()
                        .success(true)
                        .message("RÉ™y uÄŸurla É™lavÉ™ edildi")
                        .data(response)
                        .build());
    }
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
}