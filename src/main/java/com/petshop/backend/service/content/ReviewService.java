package com.petshop.backend.service.content;

import com.petshop.backend.dto.request.common.AddReviewRequest;
import com.petshop.backend.dto.request.common.ReviewReplyRequest;
import com.petshop.backend.dto.request.common.UpdateReviewRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.common.ReviewResponse;
import com.petshop.backend.dto.response.common.ReviewSummaryResponse;
import com.petshop.backend.security.UserPrincipal;

public interface ReviewService {
    PageResponse<ReviewResponse> getReviewsByTarget(String targetType, Long targetId, String sort, int page, int size);
    ReviewSummaryResponse getSummary(String targetType, Long targetId);
    PageResponse<ReviewResponse> getMyReviews(UserPrincipal currentUser, int page, int size);
    PageResponse<ReviewResponse> getVendorReviews(UserPrincipal currentUser, int page, int size);
    ReviewResponse addReview(AddReviewRequest request, UserPrincipal currentUser);
    ReviewResponse updateReview(Long id, UpdateReviewRequest request, UserPrincipal currentUser);
    ReviewResponse replyToReview(Long id, ReviewReplyRequest request, UserPrincipal currentUser);
    void deleteReview(Long id, UserPrincipal currentUser);
    PageResponse<ReviewResponse> getAllReviewsForAdmin(String targetType, int page, int size);
    void deleteReviewByAdmin(Long id);
}
