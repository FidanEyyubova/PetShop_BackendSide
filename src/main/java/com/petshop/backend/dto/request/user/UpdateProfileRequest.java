package com.petshop.backend.dto.request.user;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.petshop.backend.common.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(min = 2, max = 100, message = "Full name must be 2-100 characters")
    private String fullName;

    @JsonAlias("phoneNum")
    @Pattern(regexp = ValidationPatterns.PHONE, message = ValidationPatterns.PHONE_MESSAGE)
    private String phone;

    @JsonAlias("avatar")
    @Pattern(regexp = ValidationPatterns.IMAGE_URL, message = ValidationPatterns.IMAGE_URL_MESSAGE)
    @Size(max = 1000)
    @Schema(description = "URL from POST /api/v1/files; also accepted as `avatar`")
    private String avatarUrl;

    @Size(max = 255)
    private String cityAddress;
}
