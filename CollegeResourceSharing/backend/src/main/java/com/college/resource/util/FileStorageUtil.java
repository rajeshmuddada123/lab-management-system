package com.college.resource.util;

import com.college.resource.entity.FileAttachment;
import com.college.resource.entity.User;
import com.college.resource.enums.FileType;
import com.college.resource.repository.FileAttachmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Component
public class FileStorageUtil {

    private static final Set<String> ALLOWED_MIME = Set.of(
            "image/jpeg","image/png","image/webp","image/gif");

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    private final FileAttachmentRepository fileAttachmentRepository;

    public FileStorageUtil(FileAttachmentRepository fileAttachmentRepository) {
        this.fileAttachmentRepository = fileAttachmentRepository;
    }

    public FileAttachment store(MultipartFile file, FileType fileType, User uploader) throws IOException {
        // Validate MIME type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME.contains(contentType)) {
            throw new IllegalArgumentException("Only JPEG, PNG, WebP, GIF images are allowed");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size must not exceed 10 MB");
        }

        // Build server-side path with UUID — never use original filename as path
        String subDir = fileType.name().toLowerCase().replace('_', '/');
        String uuid   = UUID.randomUUID().toString();
        String ext    = getExtension(file.getOriginalFilename());
        String relativePath = subDir + "/" + uuid + ext;

        Path targetDir  = Paths.get(uploadDir, subDir);
        Files.createDirectories(targetDir);
        Path targetFile = Paths.get(uploadDir, relativePath);
        Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        FileAttachment attachment = FileAttachment.builder()
                .storagePath(relativePath)
                .originalFilename(sanitise(file.getOriginalFilename()))
                .fileType(fileType)
                .mimeType(contentType)
                .fileSizeBytes(file.getSize())
                .uploadedBy(uploader)
                .build();
        return fileAttachmentRepository.save(attachment);
    }

    private String getExtension(String filename) {
        if (filename == null) return ".jpg";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot).toLowerCase() : ".jpg";
    }

    private String sanitise(String name) {
        return name == null ? "file" : name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    public Path resolvePath(String storagePath) {
        return Paths.get(uploadDir).resolve(storagePath).normalize();
    }
}
