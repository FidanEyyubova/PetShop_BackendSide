package com.petshop.backend.repository.file;

import com.petshop.backend.entity.file.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {

    Optional<UploadedFile> findByStorageKey(String storageKey);
}
