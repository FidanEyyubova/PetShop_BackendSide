package com.petshop.backend.repository.product;

import com.petshop.backend.entity.product.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByOwnerId(Long ownerId);
    Optional<Store> findBySlug(String slug);
}
