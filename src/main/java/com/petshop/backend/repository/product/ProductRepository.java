package com.petshop.backend.repository.product;

import com.petshop.backend.entity.product.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlugAndDeletedAtIsNull(String slug);

    Optional<Product> findByIdAndDeletedAtIsNull(Long id);

    boolean existsBySlug(String slug);

    boolean existsByStoreIdAndSkuIgnoreCaseAndDeletedAtIsNull(Long storeId, String sku);

    boolean existsByStoreIdAndSkuIgnoreCaseAndDeletedAtIsNullAndIdNot(Long storeId, String sku, Long id);

    /** Locks the rows for the stock check-and-decrement during checkout. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id IN :ids ORDER BY p.id")
    List<Product> findAllByIdForUpdate(@Param("ids") Collection<Long> ids);

    @Query("SELECT p.id FROM Product p WHERE p.deletedAt IS NULL AND p.isActive = true AND p.soldCount > 0 " +
           "ORDER BY p.soldCount DESC, p.rating DESC")
    List<Long> findBestSellerIds(Pageable pageable);

    /** Same category first, then same pet type. Pass -1 / "" when the product has no category / pet type. */
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.isActive = true AND p.id <> :id " +
           "AND (p.categoryId = :categoryId OR p.petType = :petType) " +
           "ORDER BY CASE WHEN p.categoryId = :categoryId THEN 0 ELSE 1 END, p.soldCount DESC, p.createdAt DESC")
    List<Product> findRelated(@Param("id") Long id, @Param("categoryId") Long categoryId,
                              @Param("petType") String petType, Pageable pageable);

    long countByStoreIdAndDeletedAtIsNullAndIsActiveTrue(Long storeId);

    long countByVendorIdAndDeletedAtIsNull(Long vendorId);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.vendorId = :vendorId AND p.deletedAt IS NULL " +
           "AND p.stockQuantity > 0 AND p.stockQuantity <= :threshold")
    long countLowStock(@Param("vendorId") Long vendorId, @Param("threshold") int threshold);

    @Query("SELECT p.petType, COUNT(p) FROM Product p WHERE p.deletedAt IS NULL AND p.isActive = true " +
           "AND p.petType IS NOT NULL GROUP BY p.petType")
    List<Object[]> countActiveByPetType();

    @Query("SELECT p.categoryId, COUNT(p) FROM Product p WHERE p.deletedAt IS NULL AND p.isActive = true " +
           "AND p.categoryId IS NOT NULL GROUP BY p.categoryId")
    List<Object[]> countActiveByCategory();

    @Modifying
    @Query("UPDATE Product p SET p.rating = :rating, p.reviewCount = :count WHERE p.id = :id")
    void updateRating(@Param("id") Long id, @Param("rating") java.math.BigDecimal rating, @Param("count") int count);
}
