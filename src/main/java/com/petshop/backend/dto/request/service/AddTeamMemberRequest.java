package com.petshop.backend.dto.request.service;

import com.petshop.backend.i18n.TranslatableRequest;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.petshop.backend.common.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddTeamMemberRequest implements TranslatableRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 150)
    @JsonAlias("name")
    private String fullName;

    @NotBlank(message = "Role is required")
    @Size(max = 100)
    @Schema(example = "Senior groomer")
    private String role;

    @NotBlank(message = "Experience is required")
    @Size(max = 255)
    @Schema(example = "6 years")
    private String experience;

    @Size(max = 1000)
    private String certifications;

    @Size(max = 2000)
    private String bio;

    @Size(max = 2000)
    private String note;

    @JsonAlias("photo")
    @Pattern(regexp = ValidationPatterns.IMAGE_URL, message = ValidationPatterns.IMAGE_URL_MESSAGE)
    @Size(max = 1000)
    private String avatar;

    @Schema(description = "Show on the public page (max 4 featured members)")
    private Boolean featured;

    @Schema(description = TRANSLATIONS_DOC + " Fields: role, experience, certifications, bio, note")
    private Map<String, Map<String, Object>> translations;
}
