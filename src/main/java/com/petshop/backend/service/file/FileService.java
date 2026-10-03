package com.petshop.backend.service.file;

import com.petshop.backend.config.AppProperties;
import com.petshop.backend.dto.response.file.FileUploadResponse;
import com.petshop.backend.entity.file.UploadedFile;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.exception.ResourceNotFoundException;
import com.petshop.backend.repository.file.UploadedFileRepository;
import com.petshop.backend.security.CurrentUser;
import com.petshop.backend.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * Validates and stores images. The MIME type is detected from the file's magic bytes, not from the
 * client-supplied Content-Type; only PNG, JPEG and WebP up to 5 MB are accepted (SVG is rejected).
 */
@Service
public class FileService {

    private final StorageService storage;
    private final UploadedFileRepository repository;
    private final long maxBytes;

    public FileService(StorageService storage, UploadedFileRepository repository, AppProperties properties) {
        this.storage = storage;
        this.repository = repository;
        this.maxBytes = properties.getStorage().getMaxFileSizeBytes();
    }

    @Transactional
    public FileUploadResponse upload(MultipartFile file, UserPrincipal currentUser) {
        CurrentUser.require(currentUser);
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("EMPTY_FILE", "File is empty");
        }
        if (file.getSize() > maxBytes) {
            throw new BadRequestException("FILE_TOO_LARGE", "File is too large (max " + (maxBytes / 1024 / 1024) + " MB)");
        }
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new BadRequestException("INVALID_UPLOAD", "Could not read the uploaded file");
        }
        ImageType type = ImageType.detect(bytes)
                .orElseThrow(() -> new BadRequestException("UNSUPPORTED_FILE_TYPE",
                        "Only PNG, JPEG and WebP images are allowed"));

        String key = UUID.randomUUID().toString().replace("-", "") + "." + type.extension;
        String url = storage.store(key, bytes, type.mimeType);

        UploadedFile saved = repository.save(UploadedFile.builder()
                .ownerId(currentUser.getId())
                .storageKey(key)
                .url(url)
                .contentType(type.mimeType)
                .sizeBytes((long) bytes.length)
                .originalName(sanitizeName(file.getOriginalFilename()))
                .build());
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id, UserPrincipal currentUser) {
        UploadedFile file = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File", "id", id));
        CurrentUser.requireOwnerOrAdmin(currentUser, file.getOwnerId(), "file");
        storage.delete(file.getStorageKey());
        repository.delete(file);
    }

    /** Bytes and content type for locally stored files. */
    @Transactional(readOnly = true)
    public Optional<StoredFile> load(String key) {
        if (key == null || !key.matches("[A-Za-z0-9._-]+")) return Optional.empty();
        return repository.findByStorageKey(key)
                .flatMap(f -> storage.load(key).map(bytes -> new StoredFile(bytes, f.getContentType())));
    }

    private FileUploadResponse toResponse(UploadedFile f) {
        return FileUploadResponse.builder()
                .id(f.getId())
                .url(f.getUrl())
                .contentType(f.getContentType())
                .sizeBytes(f.getSizeBytes())
                .originalName(f.getOriginalName())
                .createdAt(f.getCreatedAt())
                .build();
    }

    private String sanitizeName(String name) {
        if (name == null) return null;
        String clean = name.replaceAll("[^A-Za-z0-9._ -]", "_");
        return clean.length() > 200 ? clean.substring(0, 200) : clean;
    }

    public record StoredFile(byte[] bytes, String contentType) {}

    enum ImageType {
        PNG("image/png", "png"),
        JPEG("image/jpeg", "jpg"),
        WEBP("image/webp", "webp");

        final String mimeType;
        final String extension;

        ImageType(String mimeType, String extension) {
            this.mimeType = mimeType;
            this.extension = extension;
        }

        static Optional<ImageType> detect(byte[] b) {
            if (b.length >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') {
                return Optional.of(PNG);
            }
            if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
                return Optional.of(JPEG);
            }
            if (b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                    && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
                return Optional.of(WEBP);
            }
            return Optional.empty();
        }
    }
}
