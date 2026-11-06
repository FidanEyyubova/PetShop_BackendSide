package com.petshop.backend.service.content.impl;

import com.petshop.backend.common.ReviewTargetType;
import com.petshop.backend.common.Roles;
import com.petshop.backend.dto.request.common.AddReviewRequest;
import com.petshop.backend.dto.request.common.ReviewReplyRequest;
import com.petshop.backend.dto.request.common.UpdateReviewRequest;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.common.ReviewResponse;
import com.petshop.backend.dto.response.common.ReviewSummaryResponse;
import com.petshop.backend.entity.content.Review;
import com.petshop.backend.entity.product.Product;
import com.petshop.backend.entity.product.Store;
import com.petshop.backend.entity.service.ServiceProviderProfile;
import com.petshop.backend.entity.user.User;
import com.petshop.backend.exception.AccessDeniedCustomException;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.ConflictException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.content.ReviewRepository;
import com.petshop.backend.repository.order.OrderRepository;
import com.petshop.backend.repository.product.ProductRepository;
import com.petshop.backend.repository.product.StoreRepository;
import com.petshop.backend.repository.service.ServiceProviderProfileRepository;
import com.petshop.backend.repository.user.UserRepository;
import com.petshop.backend.security.CurrentUser;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.content.ReviewService;
import com.petshop.backend.service.product.ProductMapper;
import com.petshop.backend.util.PageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final ServiceProviderProfileRepository profileRepository;
    private final OrderRepository orderRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository,
                             ProductRepository productRepository, StoreRepository storeRepository,
                             ServiceProviderProfileRepository profileRepository, OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.profileRepository = profileRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getReviewsByTarget(String targetType, Long targetId, String sort, int page, int size) {
        ReviewTargetType type = ReviewTargetType.parse(targetType);
        if (type == null || targetId == null) {
            throw new BadRequestException("MISSING_PARAMETER", "targetType and targetId are required");
        }
        Sort order = switch (sort == null ? "newest" : sort.toLowerCase(Locale.ROOT)) {
            case "highest", "rating-high" -> Sort.by(Sort.Direction.DESC, "rating", "createdAt");
            case "lowest", "rating-low" -> Sort.by(Sort.Direction.ASC, "rating").and(Sort.by(Sort.Direction.DESC, "createdAt"));
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        Page<Review> result = reviewRepository.findByTargetTypeAndTargetId(type.name(), targetId, PageUtils.of(page, size, order));
        return PageResponse.of(result, toResponses(result.getContent()));
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewSummaryResponse getSummary(String targetType, Long targetId) {
        ReviewTargetType type = ReviewTargetType.parse(targetType);
        if (type == null || targetId == null) {
            throw new BadRequestException("MISSING_PARAMETER", "targetType and targetId are required");
        }
        return summarize(type, targetId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getMyReviews(UserPrincipal currentUser, int page, int size) {
        CurrentUser.require(currentUser);
        Page<Review> result = reviewRepository.findByUserId(currentUser.getId(),
                PageUtils.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return PageResponse.of(result, toResponses(result.getContent()));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getVendorReviews(UserPrincipal currentUser, int page, int size) {
        CurrentUser.require(currentUser);
        Long storeId = storeRepository.findFirstByOwnerIdOrderByIdAsc(currentUser.getId()).map(Store::getId).orElse(-1L);
        List<Long> productIds = productRepository.findAll((root, q, cb) -> cb.equal(root.get("vendorId"), currentUser.getId()))
                .stream().map(Product::getId).toList();
        var pageable = PageUtils.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> result = productIds.isEmpty()
                ? reviewRepository.findForStoreOnly(storeId, pageable)
                : reviewRepository.findForVendor(productIds, storeId, pageable);
        return PageResponse.of(result, toResponses(result.getContent()));
    }

    @Override
    @Transactional
    public ReviewResponse addReview(AddReviewRequest request, UserPrincipal currentUser) {
        CurrentUser.require(currentUser);
        ReviewTargetType type = ReviewTargetType.parse(request.getTargetType());
        Long targetId = request.getTargetId();
        Long ownerId = targetOwner(type, targetId);
        if (Objects.equals(ownerId, currentUser.getId())) {
            throw new AccessDeniedCustomException("OWN_TARGET", "You cannot review your own " + type.name().toLowerCase());
        }
        if (reviewRepository.existsByUserIdAndTargetTypeAndTargetId(currentUser.getId(), type.name(), targetId)) {
            throw new ConflictException("DUPLICATE_REVIEW", "You have already reviewed this " + type.name().toLowerCase()
                    + "; edit your existing review instead");
        }
        boolean verified = switch (type) {
            case PRODUCT -> orderRepository.hasDeliveredProduct(currentUser.getId(), targetId);
            case STORE -> orderRepository.hasDeliveredFromStore(currentUser.getId(), targetId);
            case SERVICE_PROVIDER -> false;
        };
        if (type != ReviewTargetType.SERVICE_PROVIDER && !verified) {
            throw new AccessDeniedCustomException("REVIEW_REQUIRES_PURCHASE",
                    "You can review a " + type.name().toLowerCase() + " after a delivered order");
        }
        Review saved = reviewRepository.save(Review.builder()
                .userId(currentUser.getId())
                .targetType(type.name())
                .targetId(targetId)
                .rating(request.getRating())
                .comment(request.getComment().trim())
                .petName(blank(request.getPetName()))
                .petPhoto(blank(request.getPetPhoto()))
                .verifiedPurchase(verified)
                .build());
        recalculate(type, targetId);
        return toResponses(List.of(saved)).get(0);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long id, UpdateReviewRequest request, UserPrincipal currentUser) {
        Review review = find(id);
        CurrentUser.require(currentUser);
        if (!review.getUserId().equals(currentUser.getId())) {
            throw new AccessDeniedCustomException("NOT_OWNER", "You can only edit your own reviews");
        }
        if (request.getRating() != null) review.setRating(request.getRating());
        if (request.getComment() != null) review.setComment(request.getComment().trim());
        if (request.getPetName() != null) review.setPetName(blank(request.getPetName()));
        if (request.getPetPhoto() != null) review.setPetPhoto(blank(request.getPetPhoto()));
        Review saved = reviewRepository.save(review);
        recalculate(ReviewTargetType.parse(saved.getTargetType()), saved.getTargetId());
        return toResponses(List.of(saved)).get(0);
    }

    @Override
    @Transactional
    public ReviewResponse replyToReview(Long id, ReviewReplyRequest request, UserPrincipal currentUser) {
        Review review = find(id);
        CurrentUser.require(currentUser);
        Long ownerId = targetOwner(ReviewTargetType.parse(review.getTargetType()), review.getTargetId());
        if (!currentUser.isAdmin() && !Objects.equals(ownerId, currentUser.getId())) {
            throw new AccessDeniedCustomException("NOT_OWNER", "Only the owner of the reviewed item can reply");
        }
        review.setProviderReply(request.getProviderReply().trim());
        review.setReplyDate(LocalDateTime.now());
        return toResponses(List.of(reviewRepository.save(review))).get(0);
    }

    @Override
    @Transactional
    public void deleteReview(Long id, UserPrincipal currentUser) {
        Review review = find(id);
        CurrentUser.require(currentUser);
        if (!currentUser.isAdmin() && !review.getUserId().equals(currentUser.getId())) {
            throw new AccessDeniedCustomException("NOT_OWNER", "You can only delete your own reviews");
        }
        reviewRepository.delete(review);
        reviewRepository.flush();
        recalculate(ReviewTargetType.parse(review.getTargetType()), review.getTargetId());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getAllReviewsForAdmin(String targetType, int page, int size) {
        var pageable = PageUtils.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        ReviewTargetType type = ReviewTargetType.parse(targetType);
        Page<Review> result = type == null ? reviewRepository.findAll(pageable) : reviewRepository.findByTargetType(type.name(), pageable);
        return PageResponse.of(result, toResponses(result.getContent()));
    }

    @Override
    @Transactional
    public void deleteReviewByAdmin(Long id) {
        Review review = find(id);
        reviewRepository.delete(review);
        reviewRepository.flush();
        recalculate(ReviewTargetType.parse(review.getTargetType()), review.getTargetId());
    }

    private Review find(Long id) {
        return reviewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
    }

    private Long targetOwner(ReviewTargetType type, Long targetId) {
        return switch (type) {
            case PRODUCT -> productRepository.findByIdAndDeletedAtIsNull(targetId)
                    .map(Product::getVendorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", targetId));
            case STORE -> storeRepository.findById(targetId)
                    .map(Store::getOwnerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Store", "id", targetId));
            case SERVICE_PROVIDER -> {
                User provider = userRepository.findById(targetId)
                        .filter(u -> Roles.ROLE_SERVICE_PROVIDER.equals(Roles.normalize(u.getRole())) && !u.isDeleted())
                        .orElseThrow(() -> new ResourceNotFoundException("Service provider", "id", targetId));
                yield provider.getId();
            }
        };
    }

    private ReviewSummaryResponse summarize(ReviewTargetType type, Long targetId) {
        Map<Integer, Long> distribution = new LinkedHashMap<>();
        for (int s = 5; s >= 1; s--) distribution.put(s, 0L);
        long count = 0;
        long total = 0;
        for (Object[] row : reviewRepository.countByRating(type.name(), targetId)) {
            int stars = ((Number) row[0]).intValue();
            long n = ((Number) row[1]).longValue();
            if (stars < 1 || stars > 5) continue;
            distribution.put(stars, n);
            count += n;
            total += (long) stars * n;
        }
        Map<Integer, Integer> percentages = new LinkedHashMap<>();
        for (Map.Entry<Integer, Long> e : distribution.entrySet()) {
            percentages.put(e.getKey(), count == 0 ? 0 : (int) Math.round(e.getValue() * 100.0 / count));
        }
        BigDecimal average = count == 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(total).divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        return ReviewSummaryResponse.builder()
                .targetType(type.name())
                .targetId(targetId)
                .average(average)
                .count(count)
                .distribution(distribution)
                .percentages(percentages)
                .build();
    }

    private void recalculate(ReviewTargetType type, Long targetId) {
        if (type == null) return;
        ReviewSummaryResponse s = summarize(type, targetId);
        int count = (int) s.getCount();
        switch (type) {
            case PRODUCT -> productRepository.updateRating(targetId, s.getAverage(), count);
            case STORE -> storeRepository.updateRating(targetId, s.getAverage(), count);
            case SERVICE_PROVIDER -> profileRepository.updateRating(targetId, s.getAverage(), count);
        }
    }

    private List<ReviewResponse> toResponses(List<Review> reviews) {
        if (reviews.isEmpty()) return List.of();
        Map<Long, User> users = userRepository.findByIdIn(reviews.stream().map(Review::getUserId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(User::getId, Function.identity()));

        Set<Long> productIds = idsOf(reviews, ReviewTargetType.PRODUCT);
        Set<Long> storeIds = idsOf(reviews, ReviewTargetType.STORE);
        Set<Long> providerIds = idsOf(reviews, ReviewTargetType.SERVICE_PROVIDER);
        Map<Long, Product> products = productIds.isEmpty() ? Map.of()
                : productRepository.findAllById(productIds).stream().collect(Collectors.toMap(Product::getId, Function.identity()));
        products.values().forEach(p -> { if (p.getStoreId() != null) storeIds.add(p.getStoreId()); });
        Map<Long, Store> stores = storeIds.isEmpty() ? Map.of()
                : storeRepository.findAllById(storeIds).stream().collect(Collectors.toMap(Store::getId, Function.identity()));
        Map<Long, ServiceProviderProfile> providers = new HashMap<>();
        for (Long pid : providerIds) profileRepository.findByProviderId(pid).ifPresent(p -> providers.put(pid, p));

        List<ReviewResponse> result = new ArrayList<>();
        for (Review r : reviews) {
            User author = users.get(r.getUserId());
            String targetName = null;
            String storeName = null;
            switch (r.getTargetType() == null ? "" : r.getTargetType()) {
                case "PRODUCT" -> {
                    Product p = products.get(r.getTargetId());
                    if (p != null) {
                        targetName = ProductMapper.localizedTitle(p);
                        Store s = p.getStoreId() == null ? null : stores.get(p.getStoreId());
                        storeName = s != null ? s.getName() : null;
                    }
                }
                case "STORE" -> {
                    Store s = stores.get(r.getTargetId());
                    targetName = s != null ? s.getName() : null;
                    storeName = targetName;
                }
                case "SERVICE_PROVIDER" -> {
                    ServiceProviderProfile p = providers.get(r.getTargetId());
                    targetName = p != null ? p.getCenterName() : null;
                }
                default -> { }
            }
            result.add(ReviewResponse.builder()
                    .id(r.getId())
                    .userId(r.getUserId())
                    .userName(author != null ? author.getFullName() : "Deleted user")
                    .userAvatar(author != null ? author.getAvatarUrl() : null)
                    .targetType(r.getTargetType())
                    .targetId(r.getTargetId())
                    .targetName(targetName)
                    .storeName(storeName)
                    .rating(r.getRating())
                    .comment(r.getComment())
                    .petName(r.getPetName())
                    .petPhoto(r.getPetPhoto())
                    .verifiedPurchase(Boolean.TRUE.equals(r.getVerifiedPurchase()))
                    .reply(r.getProviderReply())
                    .replyDate(r.getReplyDate())
                    .createdAt(r.getCreatedAt())
                    .updatedAt(r.getUpdatedAt())
                    .build());
        }
        return result;
    }

    private static Set<Long> idsOf(List<Review> reviews, ReviewTargetType type) {
        return reviews.stream().filter(r -> type.name().equals(r.getTargetType()))
                .map(Review::getTargetId).collect(Collectors.toCollection(HashSet::new));
    }

    private static String blank(String v) {
        return v == null || v.isBlank() ? null : v.trim();
    }
}
