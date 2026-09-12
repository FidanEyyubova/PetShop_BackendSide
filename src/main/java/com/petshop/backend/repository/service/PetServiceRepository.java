package com.petshop.backend.repository.service;

import com.petshop.backend.entity.service.PetService;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PetServiceRepository extends JpaRepository<PetService, Long> {
    List<PetService> findByProviderId(Long providerId);
    List<PetService> findByEmergencyServiceTrue();
}
