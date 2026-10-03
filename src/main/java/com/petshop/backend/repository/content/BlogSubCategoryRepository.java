package com.petshop.backend.repository.content;

import com.petshop.backend.entity.content.BlogSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogSubCategoryRepository extends JpaRepository<BlogSubCategory, Long> {

    Optional<BlogSubCategory> findBySlugIgnoreCase(String slug);

    boolean existsBySlug(String slug);

    List<BlogSubCategory> findAllByOrderByTitleAsc();
}
