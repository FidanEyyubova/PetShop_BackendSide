package com.petshop.backend.service.service;

import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.service.ServiceResponse;

public interface PetServiceService {
    PageResponse<ServiceResponse> getAllServices(String q, String serviceType, Long providerId, Boolean emergency, int page, int size);
    ServiceResponse getServiceById(Long id);
}
