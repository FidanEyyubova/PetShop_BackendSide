package com.petshop.backend.service.content;

import com.petshop.backend.dto.request.content.HeroSectionRequest;
import com.petshop.backend.dto.response.content.HeroSectionResponse;

import java.util.List;

public interface HeroSectionService {
    HeroSectionResponse createHeroSection(HeroSectionRequest request);
    List<HeroSectionResponse> getAllHeroSections();
    List<HeroSectionResponse> getActiveHeroSections();
    HeroSectionResponse getHeroSectionById(Long id);
    HeroSectionResponse updateHeroSection(Long id, HeroSectionRequest request);
    void deleteHeroSection(Long id);
}