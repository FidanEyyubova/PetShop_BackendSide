package com.petshop.backend.repository.content;

import com.petshop.backend.entity.content.BlogSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogSubCategoryRepository extends JpaRepository<BlogSubCategory, Long> {
}