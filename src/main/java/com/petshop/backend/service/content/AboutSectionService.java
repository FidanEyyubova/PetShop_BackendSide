package com.petshop.backend.service.content;

import com.petshop.backend.dto.request.content.AboutSectionRequest;
import com.petshop.backend.dto.response.content.AboutSectionResponse;

import java.util.List;

public interface AboutSectionService {
    AboutSectionResponse createAboutSection(AboutSectionRequest request);
    List<AboutSectionResponse> getAllAboutSections();
    AboutSectionResponse getAboutSectionById(Long id);
    AboutSectionResponse updateAboutSection(Long id, AboutSectionRequest request);
    void deleteAboutSection(Long id);
}