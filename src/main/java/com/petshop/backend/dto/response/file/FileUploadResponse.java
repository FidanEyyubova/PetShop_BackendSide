package com.petshop.backend.dto.response.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Uploaded file. Put `url` into any image field (avatarUrl, logoUrl, images[] ...).")
public class FileUploadResponse {
    private Long id;
    @Schema(example = "/api/v1/files/6f1c2b0e9d7a4c1b8e3f5a2d4c6b8a0e.webp")
    private String url;
    @Schema(example = "image/webp")
    private String contentType;
    private Long sizeBytes;
    private String originalName;
    private LocalDateTime createdAt;
}
