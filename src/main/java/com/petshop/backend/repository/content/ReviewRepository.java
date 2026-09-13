package com.petshop.backend.repository.content;

import com.petshop.backend.entity.content.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTargetTypeAndTargetId(String targetType, Long targetId);
    Page<Review> findByTargetTypeAndTargetId(String targetType, Long targetId, Pageable pageable);
    Page<Review> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.targetType = :targetType AND r.targetId = :targetId")
    Double getAverageRating(@Param("targetType") String targetType, @Param("targetId") Long targetId);
}