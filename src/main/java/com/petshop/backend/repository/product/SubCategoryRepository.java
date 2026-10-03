package com.petshop.backend.repository.product;

import com.petshop.backend.entity.product.SubCategory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    @EntityGraph(attributePaths = "category")
    List<SubCategory> findByCategoryId(Long categoryId);

    @EntityGraph(attributePaths = "category")
    List<SubCategory> findAllByOrderByTitleAsc();
}
