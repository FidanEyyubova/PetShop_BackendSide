package com.petshop.backend.service.file;

import java.util.Optional;

public interface StorageService {

    String store(String key, byte[] content, String contentType);

    Optional<byte[]> load(String key);

    void delete(String key);
}
