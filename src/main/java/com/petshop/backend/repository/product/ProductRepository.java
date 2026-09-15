package com.petshop.backend.repository.product;

import com.petshop.backend.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySlug(String slug);
    List<Product> findByStoreId(Long storeId);
    List<Product> findByVendorId(Long vendorId);
    List<Product> findByStoreIdAndStockQuantityLessThan(Long storeId, Integer threshold);

    @Query("SELECT p FROM Product p WHERE " +
           "(CAST(:search AS string) IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) AND " +
           "(:categoryId IS NULL OR p.categoryId = :categoryId) AND " +
           "(:subCategoryId IS NULL OR p.subCategoryId = :subCategoryId) AND " +
           "(CAST(:petType AS string) IS NULL OR LOWER(p.petType) = LOWER(CAST(:petType AS string))) AND " +
           "(CAST(:categoryName AS string) IS NULL OR LOWER(p.categoryName) = LOWER(CAST(:categoryName AS string))) AND " +
           "(CAST(:brand AS string) IS NULL OR LOWER(p.brand) = LOWER(CAST(:brand AS string))) AND " +
           "(:storeId IS NULL OR p.storeId = :storeId) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:inStock IS NULL OR (:inStock = true AND p.stockQuantity > 0) OR (:inStock = false AND (p.stockQuantity IS NULL OR p.stockQuantity = 0)))")
    Page<Product> filterProducts(
            @Param("search") String search,
            @Param("categoryId") Long categoryId,
            @Param("subCategoryId") Long subCategoryId,
            @Param("petType") String petType,
            @Param("categoryName") String categoryName,
            @Param("brand") String brand,
            @Param("storeId") Long storeId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStock") Boolean inStock,
            Pageable pageable
    );
}