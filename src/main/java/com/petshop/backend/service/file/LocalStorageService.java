package com.petshop.backend.service.file;

import com.petshop.backend.config.AppProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
@ConditionalOnProperty(prefix = "app.storage", name = "type", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    private final Path root;
    private final String publicBaseUrl;

    public LocalStorageService(AppProperties properties) {
        this.root = Path.of(properties.getStorage().getLocalDir()).toAbsolutePath().normalize();
        String base = properties.getStorage().getPublicBaseUrl();
        this.publicBaseUrl = base == null ? "" : base.replaceAll("/+$", "");
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot create upload directory " + root, e);
        }
    }

    @Override
    public String store(String key, byte[] content, String contentType) {
        try {
            Files.write(resolve(key), content);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot store file", e);
        }
        return publicBaseUrl + "/api/v1/files/" + key;
    }

    @Override
    public Optional<byte[]> load(String key) {
        Path path = resolve(key);
        if (!Files.isRegularFile(path)) return Optional.empty();
        try {
            return Optional.of(Files.readAllBytes(path));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(String key) {
        try {
            Files.deleteIfExists(resolve(key));
        } catch (IOException ignored) {
        }
    }

    private Path resolve(String key) {
        Path path = root.resolve(key).normalize();
        if (!path.startsWith(root)) {
            throw new IllegalArgumentException("Invalid file key");
        }
        return path;
    }
}
