package com.petshop.backend.repository.service;

import com.petshop.backend.entity.service.ProviderGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderGalleryRepository extends JpaRepository<ProviderGallery, Long> {
    List<ProviderGallery> findByProviderIdOrderByCreatedAtDesc(Long providerId);
}