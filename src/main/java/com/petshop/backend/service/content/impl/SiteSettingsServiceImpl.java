package com.petshop.backend.service.content.impl;

import com.petshop.backend.dto.request.content.SiteSettingsRequest;
import com.petshop.backend.dto.response.content.SiteSettingsResponse;
import com.petshop.backend.entity.content.SiteSettings;
import com.petshop.backend.repository.content.SiteSettingsRepository;
import com.petshop.backend.service.content.SiteSettingsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteSettingsServiceImpl implements SiteSettingsService {

    private final SiteSettingsRepository repository;

    public SiteSettingsServiceImpl(SiteSettingsRepository repository) {
        this.repository = repository;
    }

    @Override
    public SiteSettingsResponse getSettings() {
        List<SiteSettings> list = repository.findAll();
        if (list.isEmpty()) {
            return new SiteSettingsResponse(null, "", "", "", "", "", null);
        }
        return mapToResponse(list.get(0));
    }

    @Override
    public SiteSettingsResponse saveOrUpdateSettings(SiteSettingsRequest request) {
        List<SiteSettings> list = repository.findAll();
        SiteSettings settings = list.isEmpty() ? new SiteSettings() : list.get(0);

        settings.setInstagramUrl(request.getInstagramUrl());
        settings.setTiktokUrl(request.getTiktokUrl());
        settings.setFacebookUrl(request.getFacebookUrl());
        settings.setxUrl(request.getxUrl());
        settings.setLogoUrl(request.getLogoUrl());

        SiteSettings saved = repository.save(settings);
        return mapToResponse(saved);
    }

    private SiteSettingsResponse mapToResponse(SiteSettings s) {
        return new SiteSettingsResponse(
                s.getId(),
                s.getInstagramUrl(),
                s.getTiktokUrl(),
                s.getFacebookUrl(),
                s.getxUrl(),
                s.getLogoUrl(),
                s.getUpdatedAt()
        );
    }
}