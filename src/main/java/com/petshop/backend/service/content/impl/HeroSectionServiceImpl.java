package com.petshop.backend.service.content.impl;

import com.petshop.backend.i18n.Localized;
import com.petshop.backend.i18n.TranslatableFields;

import com.petshop.backend.dto.request.content.HeroSectionRequest;
import com.petshop.backend.dto.response.content.HeroSectionResponse;
import com.petshop.backend.entity.content.HeroSection;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.content.HeroSectionRepository;
import com.petshop.backend.service.content.HeroSectionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HeroSectionServiceImpl implements HeroSectionService {

    private final HeroSectionRepository heroSectionRepository;

    public HeroSectionServiceImpl(HeroSectionRepository heroSectionRepository) {
        this.heroSectionRepository = heroSectionRepository;
    }

    @Override
    public HeroSectionResponse createHeroSection(HeroSectionRequest request) {
        HeroSection heroSection = new HeroSection();
        heroSection.setTitle(request.getTitle());
        heroSection.setTranslations(heroSection.getTranslations().mergedWith(request.getTranslations(), TranslatableFields.HERO));
        heroSection.setSubtitle(request.getSubtitle());
        heroSection.setBgImageUrl(request.getBgImageUrl());
        heroSection.setPrice(request.getPrice());
        heroSection.setDiscountPrice(request.getDiscountPrice());
        heroSection.setActive(request.getActive() != null ? request.getActive() : true);

        HeroSection saved = heroSectionRepository.save(heroSection);
        return mapToResponse(saved);
    }

    @Override
    public List<HeroSectionResponse> getAllHeroSections() {
        return heroSectionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<HeroSectionResponse> getActiveHeroSections() {
        return heroSectionRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public HeroSectionResponse getHeroSectionById(Long id) {
        HeroSection heroSection = heroSectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HeroSection", "id", id));
        return mapToResponse(heroSection);
    }

    @Override
    public HeroSectionResponse updateHeroSection(Long id, HeroSectionRequest request) {
        HeroSection heroSection = heroSectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HeroSection", "id", id));

        heroSection.setTitle(request.getTitle());

        heroSection.setTranslations(heroSection.getTranslations().mergedWith(request.getTranslations(), TranslatableFields.HERO));
        heroSection.setSubtitle(request.getSubtitle());
        heroSection.setBgImageUrl(request.getBgImageUrl());
        heroSection.setPrice(request.getPrice());
        heroSection.setDiscountPrice(request.getDiscountPrice());
        if (request.getActive() != null) {
            heroSection.setActive(request.getActive());
        }

        HeroSection updated = heroSectionRepository.save(heroSection);
        return mapToResponse(updated);
    }

    @Override
    public void deleteHeroSection(Long id) {
        HeroSection heroSection = heroSectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HeroSection", "id", id));
        heroSectionRepository.delete(heroSection);
    }

    private HeroSectionResponse mapToResponse(HeroSection heroSection) {
        Localized t = Localized.of(heroSection.getTranslations());
        HeroSectionResponse response = new HeroSectionResponse(
                heroSection.getId(),
                t.text("title", heroSection.getTitle()),
                t.text("subtitle", heroSection.getSubtitle()),
                heroSection.getBgImageUrl(),
                heroSection.getPrice(),
                heroSection.getDiscountPrice(),
                heroSection.getActive(),
                heroSection.getCreatedAt()
        );
        response.setTranslations(Localized.editable(heroSection.getTranslations(), "title", heroSection.getTitle(), "subtitle", heroSection.getSubtitle()));
        return response;
    }
}