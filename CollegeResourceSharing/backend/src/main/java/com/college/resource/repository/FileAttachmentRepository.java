package com.college.resource.repository;

import com.college.resource.entity.FileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FileAttachmentRepository extends JpaRepository<FileAttachment, Long> {
    Optional<FileAttachment> findByStoragePath(String storagePath);
}
