package com.petshop.backend.service.content;

import com.petshop.backend.dto.request.content.AdminServiceRequest;
import com.petshop.backend.dto.response.content.AdminServiceResponse;

import java.util.List;

public interface AdminServiceService {
    AdminServiceResponse createSiteService(AdminServiceRequest request);
    List<AdminServiceResponse> getAllSiteServices();
    List<AdminServiceResponse> getActiveSiteServices();
    AdminServiceResponse getSiteServiceById(Long id);
    AdminServiceResponse updateSiteService(Long id, AdminServiceRequest request);
    void deleteSiteService(Long id);
}