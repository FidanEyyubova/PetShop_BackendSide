package com.petshop.backend.service.content.impl;

import com.petshop.backend.dto.request.content.AboutSectionRequest;
import com.petshop.backend.dto.response.content.AboutSectionResponse;
import com.petshop.backend.entity.content.AboutSection;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.content.AboutSectionRepository;
import com.petshop.backend.service.content.AboutSectionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AboutServiceImpl implements AboutSectionService {

    private final AboutSectionRepository aboutSectionRepository;

    public AboutServiceImpl(AboutSectionRepository aboutSectionRepository) {
        this.aboutSectionRepository = aboutSectionRepository;
    }

    @Override
    public AboutSectionResponse createAboutSection(AboutSectionRequest request) {
        AboutSection section = new AboutSection();
        section.setTitle(request.getTitle());
        section.setSubtitle(request.getSubtitle());
        section.setImageUrl(request.getImageUrl());
        section.setDescription(request.getDescription());

        AboutSection saved = aboutSectionRepository.save(section);
        return mapToResponse(saved);
    }

    @Override
    public List<AboutSectionResponse> getAllAboutSections() {
        return aboutSectionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AboutSectionResponse getAboutSectionById(Long id) {
        AboutSection section = aboutSectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AboutSection", "id", id));
        return mapToResponse(section);
    }

    @Override
    public AboutSectionResponse updateAboutSection(Long id, AboutSectionRequest request) {
        AboutSection section = aboutSectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AboutSection", "id", id));

        section.setTitle(request.getTitle());
        section.setSubtitle(request.getSubtitle());
        section.setImageUrl(request.getImageUrl());
        section.setDescription(request.getDescription());

        AboutSection updated = aboutSectionRepository.save(section);
        return mapToResponse(updated);
    }

    @Override
    public void deleteAboutSection(Long id) {
        AboutSection section = aboutSectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AboutSection", "id", id));
        aboutSectionRepository.delete(section);
    }

    private AboutSectionResponse mapToResponse(AboutSection section) {
        return new AboutSectionResponse(
                section.getId(),
                section.getTitle(),
                section.getSubtitle(),
                section.getImageUrl(),
                section.getDescription(),
                section.getCreatedAt()
        );
    }
}