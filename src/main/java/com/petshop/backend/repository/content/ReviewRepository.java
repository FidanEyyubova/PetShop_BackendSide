package com.petshop.backend.repository.content;

import com.petshop.backend.entity.content.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByTargetTypeAndTargetId(String targetType, Long targetId, Pageable pageable);

    Page<Review> findByUserId(Long userId, Pageable pageable);

    Page<Review> findByTargetType(String targetType, Pageable pageable);

    long countByUserId(Long userId);

    boolean existsByUserIdAndTargetTypeAndTargetId(Long userId, String targetType, Long targetId);

    /** Rows of [rating, count] for the star distribution. */
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.targetType = :type AND r.targetId = :id GROUP BY r.rating")
    List<Object[]> countByRating(@Param("type") String targetType, @Param("id") Long targetId);

    /** Reviews on any of the given targets (vendor: products and store). */
    @Query("SELECT r FROM Review r WHERE (r.targetType = 'PRODUCT' AND r.targetId IN :productIds) " +
           "OR (r.targetType = 'STORE' AND r.targetId = :storeId)")
    Page<Review> findForVendor(@Param("productIds") Collection<Long> productIds, @Param("storeId") Long storeId,
                               Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.targetType = 'STORE' AND r.targetId = :storeId")
    Page<Review> findForStoreOnly(@Param("storeId") Long storeId, Pageable pageable);
}
