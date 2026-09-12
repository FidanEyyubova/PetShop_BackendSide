package com.petshop.backend.service.content;

import com.petshop.backend.dto.request.common.AddReviewRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.common.ReviewResponse;
import com.petshop.backend.security.UserPrincipal;
import org.springframework.lang.NonNull;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    PageResponse<ReviewResponse> getReviewsByTarget(String targetType, Long targetId, @NonNull Pageable pageable);
    ReviewResponse addReview(AddReviewRequest request, UserPrincipal currentUser);
}