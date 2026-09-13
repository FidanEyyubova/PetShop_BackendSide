package com.petshop.backend.service.content;

import com.petshop.backend.dto.request.content.SiteSettingsRequest;
import com.petshop.backend.dto.response.content.SiteSettingsResponse;

public interface SiteSettingsService {
    SiteSettingsResponse getSettings();
    SiteSettingsResponse saveOrUpdateSettings(SiteSettingsRequest request);
}