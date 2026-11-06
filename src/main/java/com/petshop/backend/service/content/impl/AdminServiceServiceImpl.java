package com.petshop.backend.service.content.impl;

import com.petshop.backend.i18n.Localized;
import com.petshop.backend.i18n.TranslatableFields;

import com.petshop.backend.dto.request.content.AdminServiceRequest;
import com.petshop.backend.dto.response.content.AdminServiceResponse;
import com.petshop.backend.entity.content.AdminServiceEntity;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.content.AdminServiceRepository;
import com.petshop.backend.service.content.AdminServiceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceServiceImpl implements AdminServiceService {

    private final AdminServiceRepository adminServiceRepository;

    public AdminServiceServiceImpl(AdminServiceRepository adminServiceRepository) {
        this.adminServiceRepository = adminServiceRepository;
    }

    @Override
    public AdminServiceResponse createSiteService(AdminServiceRequest request) {
        AdminServiceEntity entity = new AdminServiceEntity();
        entity.setTitle(request.getTitle());
        entity.setTranslations(entity.getTranslations().mergedWith(request.getTranslations(), TranslatableFields.ADMIN_SERVICE));
        entity.setSubtitle(request.getSubtitle());
        entity.setImageUrl(request.getImageUrl());
        entity.setActive(request.getActive() != null ? request.getActive() : true);

        AdminServiceEntity saved = adminServiceRepository.save(entity);
        return mapToResponse(saved);
    }

    @Override
    public List<AdminServiceResponse> getAllSiteServices() {
        return adminServiceRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminServiceResponse> getActiveSiteServices() {
        return adminServiceRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AdminServiceResponse getSiteServiceById(Long id) {
        AdminServiceEntity entity = adminServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdminService", "id", id));
        return mapToResponse(entity);
    }

    @Override
    public AdminServiceResponse updateSiteService(Long id, AdminServiceRequest request) {
        AdminServiceEntity entity = adminServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdminService", "id", id));

        entity.setTitle(request.getTitle());

        entity.setTranslations(entity.getTranslations().mergedWith(request.getTranslations(), TranslatableFields.ADMIN_SERVICE));
        entity.setSubtitle(request.getSubtitle());
        entity.setImageUrl(request.getImageUrl());
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }

        AdminServiceEntity updated = adminServiceRepository.save(entity);
        return mapToResponse(updated);
    }

    @Override
    public void deleteSiteService(Long id) {
        AdminServiceEntity entity = adminServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdminService", "id", id));
        adminServiceRepository.delete(entity);
    }

    private AdminServiceResponse mapToResponse(AdminServiceEntity entity) {
        Localized t = Localized.of(entity.getTranslations());
        AdminServiceResponse response = new AdminServiceResponse(
                entity.getId(),
                t.text("title", entity.getTitle()),
                t.text("subtitle", entity.getSubtitle()),
                entity.getImageUrl(),
                entity.getActive(),
                entity.getCreatedAt()
        );
        response.setTranslations(Localized.editable(entity.getTranslations(), "title", entity.getTitle(), "subtitle", entity.getSubtitle()));
        return response;
    }
}