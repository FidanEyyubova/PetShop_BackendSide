package com.petshop.backend.service.content.impl;

import com.petshop.backend.dto.request.common.AddReviewRequest;
import com.petshop.backend.dto.request.common.ReviewReplyRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.common.ReviewResponse;
import com.petshop.backend.entity.content.Review;
import com.petshop.backend.entity.user.User;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.content.ReviewRepository;
import com.petshop.backend.repository.user.UserRepository;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.content.ReviewService;
import org.springframework.lang.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getReviewsByTarget(String targetType, Long targetId, @NonNull Pageable pageable) {
        Page<Review> page = reviewRepository.findByTargetTypeAndTargetId(targetType, targetId, pageable);
        List<ReviewResponse> content = page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList());

        return PageResponse.<ReviewResponse>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getMyReviews(UserPrincipal currentUser, @NonNull Pageable pageable) {
        Page<Review> page = reviewRepository.findByUserId(currentUser.getId(), pageable);
        List<ReviewResponse> content = page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList());

        return PageResponse.<ReviewResponse>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional
    public ReviewResponse addReview(AddReviewRequest request, UserPrincipal currentUser) {
        if (request.getTargetType() == null || request.getTargetId() == null) {
            throw new BadRequestException("Target type and ID are required");
        }

        Review review = Review.builder()
                .userId(currentUser.getId())
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review saved = Objects.requireNonNull(reviewRepository.save(review));
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long id, AddReviewRequest request, UserPrincipal currentUser) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        if (!review.getUserId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only update your own reviews!");
        }

        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }

        Review updated = reviewRepository.save(review);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public ReviewResponse replyToReview(Long id, ReviewReplyRequest request, UserPrincipal currentUser) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        review.setProviderReply(request.getProviderReply());
        Review updated = reviewRepository.save(review);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteReview(Long id, UserPrincipal currentUser) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        if (!review.getUserId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only delete your own reviews!");
        }

        reviewRepository.delete(review);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getAllReviewsForAdmin(String search, Pageable pageable) {
        Page<Review> page = reviewRepository.findAll(pageable);
        List<ReviewResponse> content = page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList());

        return PageResponse.<ReviewResponse>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional
    public void deleteReviewByAdmin(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
        reviewRepository.delete(review);
    }

    private ReviewResponse mapToResponse(Review review) {
        User user = userRepository.findById(review.getUserId()).orElse(null);
        String name = user != null ? user.getFullName() : "User #" + review.getUserId();
        String avatar = user != null ? user.getAvatarUrl() : "";

        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .userName(name)
                .userAvatar(avatar)
                .targetType(review.getTargetType())
                .targetId(review.getTargetId())
                .rating(review.getRating())
                .comment(review.getComment())
                .providerReply(review.getProviderReply())
                .createdAt(review.getCreatedAt())
                .build();
    }
}