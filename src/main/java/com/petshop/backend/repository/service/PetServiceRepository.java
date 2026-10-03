package com.petshop.backend.repository.service;

import com.petshop.backend.entity.service.PetService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface PetServiceRepository extends JpaRepository<PetService, Long>, JpaSpecificationExecutor<PetService> {

    List<PetService> findByProviderIdOrderByIdAsc(Long providerId);

    List<PetService> findByProviderIdAndActiveTrueOrderByIdAsc(Long providerId);

    List<PetService> findByProviderIdInAndActiveTrue(Collection<Long> providerIds);
}
