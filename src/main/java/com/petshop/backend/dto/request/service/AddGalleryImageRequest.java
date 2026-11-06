package com.petshop.backend.dto.request.service;

import com.petshop.backend.i18n.TranslatableRequest;
import java.util.Map;
import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.petshop.backend.common.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddGalleryImageRequest implements TranslatableRequest {

    @NotBlank(message = "Image URL is required")
    @JsonAlias("url")
    @Pattern(regexp = ValidationPatterns.IMAGE_URL, message = ValidationPatterns.IMAGE_URL_MESSAGE)
    @Size(max = 1000)
    private String imageUrl;

    @Size(max = 150)
    private String title;

    @Schema(description = TRANSLATIONS_DOC + " Fields: title")
    private Map<String, Map<String, Object>> translations;
}
