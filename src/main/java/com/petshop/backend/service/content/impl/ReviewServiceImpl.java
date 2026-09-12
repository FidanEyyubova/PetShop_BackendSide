package com.petshop.backend.service.content.impl;

import com.petshop.backend.dto.request.common.AddReviewRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.common.ReviewResponse;
import com.petshop.backend.entity.content.Review;
import com.petshop.backend.entity.user.User;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.content.ReviewRepository;
import com.petshop.backend.repository.user.UserRepository;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.content.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PageResponse<ReviewResponse> getReviewsByTarget(String targetType, Long targetId, Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findByTargetTypeAndTargetId(targetType, targetId, pageable);

        Page<ReviewResponse> responsePage = reviewPage.map(review -> {
            User user = userRepository.findById(review.getUserId()).orElse(null);
            return mapToReviewResponse(review, user);
        });

        return PageResponse.<ReviewResponse>builder()
                .content(responsePage.getContent())
                .pageNumber(responsePage.getNumber())
                .pageSize(responsePage.getSize())
                .totalElements(responsePage.getTotalElements())
                .totalPages(responsePage.getTotalPages())
                .last(responsePage.isLast())
                .build();
    }

    @Override
    public ReviewResponse addReview(AddReviewRequest request, UserPrincipal currentUser) {
        Long userId = currentUser.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Ä°stifadÉ™Ã§i tapÄ±lmadÄ± ID: " + userId));

        Review review = Review.builder()
                .userId(userId)
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        Review savedReview = reviewRepository.save(review);

        return mapToReviewResponse(savedReview, user);
    }

    private ReviewResponse mapToReviewResponse(Review review, User user) {
        return ReviewResponse.builder()
                .id(review.getId())
                .targetType(review.getTargetType())
                .targetId(review.getTargetId())
                .rating(review.getRating() != null ? review.getRating() : 0)
                .comment(review.getComment())
                .userId(review.getUserId())
                .userName(user != null ? user.getFullName() : "Anonim")
                .userAvatar(user != null ? user.getAvatarUrl() : null)
                .createdAt(review.getCreatedAt())
                .build();
    }
}