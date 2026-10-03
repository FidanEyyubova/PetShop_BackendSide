package com.petshop.backend.repository.product;

import com.petshop.backend.entity.product.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    Optional<Category> findBySlugIgnoreCase(String slug);

    @EntityGraph(attributePaths = "subCategories")
    List<Category> findAllByOrderByNameAsc();

    @EntityGraph(attributePaths = "subCategories")
    List<Category> findByTypeIgnoreCaseOrderByNameAsc(String type);

    @EntityGraph(attributePaths = "subCategories")
    Optional<Category> findWithSubCategoriesById(Long id);
}
