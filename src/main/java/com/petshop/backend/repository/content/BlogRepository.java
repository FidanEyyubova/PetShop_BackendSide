package com.petshop.backend.repository.content;

import com.petshop.backend.entity.content.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    Optional<Blog> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("SELECT b FROM Blog b WHERE (:categoryId IS NULL OR b.categoryId = :categoryId) " +
           "AND (LOWER(b.title) LIKE :pattern OR LOWER(COALESCE(b.excerpt, '')) LIKE :pattern " +
           "OR LOWER(COALESCE(b.translationsText, '')) LIKE :pattern)")
    Page<Blog> search(@Param("categoryId") Long categoryId, @Param("pattern") String pattern, Pageable pageable);

    @Query("SELECT b FROM Blog b WHERE b.id <> :id AND (:categoryId IS NULL OR b.categoryId = :categoryId) " +
           "ORDER BY b.createdAt DESC")
    List<Blog> findRelated(@Param("id") Long id, @Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT b.categoryId, COUNT(b) FROM Blog b WHERE b.categoryId IS NOT NULL GROUP BY b.categoryId")
    List<Object[]> countByCategory();

    long countByCategoryId(Long categoryId);
}
