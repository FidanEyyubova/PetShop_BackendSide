package com.petshop.backend.repository.service;

import com.petshop.backend.entity.service.ServiceProviderProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceProviderProfileRepository extends JpaRepository<ServiceProviderProfile, Long> {
    Optional<ServiceProviderProfile> findByProviderId(Long providerId);
}