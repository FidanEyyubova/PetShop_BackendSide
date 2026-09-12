package com.petshop.backend.service.service;

import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.service.ProviderProfileResponse;
import com.petshop.backend.dto.response.service.ServiceResponse;
import org.springframework.lang.NonNull;
import org.springframework.data.domain.Pageable;

public interface PetServiceService {
    PageResponse<ServiceResponse> getAllServices(String search, Long categoryId, Boolean emergency, @NonNull Pageable pageable);
    ServiceResponse getServiceById(Long id);
    ProviderProfileResponse getProviderProfile(Long providerId);
}