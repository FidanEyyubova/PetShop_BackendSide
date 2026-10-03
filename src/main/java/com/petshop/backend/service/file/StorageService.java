package com.petshop.backend.service.file;

import java.util.Optional;

/** Stores uploaded binaries. Implementations: local disk (default) or an S3-compatible bucket. */
public interface StorageService {

    /** Saves the bytes under {@code key} and returns the public URL. */
    String store(String key, byte[] content, String contentType);

    /** Bytes for locally served files; empty for remote storage. */
    Optional<byte[]> load(String key);

    void delete(String key);
}
