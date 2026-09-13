package com.petshop.backend.service.content;

import com.petshop.backend.dto.request.common.AddReviewRequest;
import com.petshop.backend.dto.request.common.ReviewReplyRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.common.ReviewResponse;
import com.petshop.backend.security.UserPrincipal;
import org.springframework.lang.NonNull;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    PageResponse<ReviewResponse> getReviewsByTarget(String targetType, Long targetId, @NonNull Pageable pageable);
    PageResponse<ReviewResponse> getMyReviews(UserPrincipal currentUser, @NonNull Pageable pageable);
    ReviewResponse addReview(AddReviewRequest request, UserPrincipal currentUser);
    ReviewResponse updateReview(Long id, AddReviewRequest request, UserPrincipal currentUser);
    ReviewResponse replyToReview(Long id, ReviewReplyRequest request, UserPrincipal currentUser);
    void deleteReview(Long id, UserPrincipal currentUser);
    PageResponse<ReviewResponse> getAllReviewsForAdmin(String search, Pageable pageable);
    void deleteReviewByAdmin(Long id);
}