package com.petshop.backend.controller.file;

import com.petshop.backend.config.OpenApiConfig;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.file.FileUploadResponse;
import com.petshop.backend.security.UserPrincipal;
import com.petshop.backend.service.file.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "Files", description = "Image uploads (PNG, JPEG, WebP, max 5 MB). Upload first, then send the returned URL in the normal requests.")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = OpenApiConfig.BEARER)
    @Operation(summary = "Upload an image", description = "multipart/form-data with field `file`. PNG, JPEG or WebP up to 5 MB; the type is checked from the file content. Returns the URL to send in other requests.")
    public ResponseEntity<ApiResponse<FileUploadResponse>> upload(
            @Parameter(description = "Image file") @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("File uploaded", fileService.upload(file, currentUser)));
    }

    @GetMapping("/{key}")
    @SecurityRequirements
    @Operation(summary = "Get an image", description = "Serves an uploaded image (local storage).")
    public ResponseEntity<byte[]> download(@PathVariable String key) {
        return fileService.load(key)
                .map(f -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(f.contentType()))
                        .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic().immutable())
                        .header("X-Content-Type-Options", "nosniff")
                        .body(f.bytes()))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = OpenApiConfig.BEARER)
    @Operation(summary = "Delete an image", description = "Owner or admin only.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id,
                                                    @AuthenticationPrincipal UserPrincipal currentUser) {
        fileService.delete(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("File deleted"));
    }
}
