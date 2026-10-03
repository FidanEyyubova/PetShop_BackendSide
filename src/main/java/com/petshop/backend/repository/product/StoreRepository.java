package com.petshop.backend.repository.product;

import com.petshop.backend.entity.product.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long>, JpaSpecificationExecutor<Store> {

    Optional<Store> findFirstByOwnerIdOrderByIdAsc(Long ownerId);

    boolean existsByOwnerId(Long ownerId);

    Optional<Store> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Modifying
    @Query("UPDATE Store s SET s.rating = :rating, s.reviewCount = :count WHERE s.id = :id")
    void updateRating(@Param("id") Long id, @Param("rating") BigDecimal rating, @Param("count") int count);
}
