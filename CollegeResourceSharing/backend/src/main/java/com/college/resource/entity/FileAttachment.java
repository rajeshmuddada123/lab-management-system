package com.college.resource.entity;

import com.college.resource.enums.FileType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "file_attachments")
public class FileAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "storage_path", nullable = false, unique = true, length = 500)
    private String storagePath;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false, length = 30)
    private FileType fileType;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "file_size_bytes", nullable = false)
    private Long fileSizeBytes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

@PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public FileAttachment() {
    }

    public FileAttachment(Long fileId, String storagePath, String originalFilename, FileType fileType, String mimeType, Long fileSizeBytes, User uploadedBy, LocalDateTime createdAt) {
        this.fileId = fileId;
        this.storagePath = storagePath;
        this.originalFilename = originalFilename;
        this.fileType = fileType;
        this.mimeType = mimeType;
        this.fileSizeBytes = fileSizeBytes;
        this.uploadedBy = uploadedBy;
        this.createdAt = createdAt;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static  Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long fileId;
        private String storagePath;
        private String originalFilename;
        private FileType fileType;
        private String mimeType;
        private Long fileSizeBytes;
        private User uploadedBy;
        private LocalDateTime createdAt;
        public Builder fileId(Long fileId) {
            this.fileId = fileId;
            return this;
        }
        public Builder storagePath(String storagePath) {
            this.storagePath = storagePath;
            return this;
        }
        public Builder originalFilename(String originalFilename) {
            this.originalFilename = originalFilename;
            return this;
        }
        public Builder fileType(FileType fileType) {
            this.fileType = fileType;
            return this;
        }
        public Builder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }
        public Builder fileSizeBytes(Long fileSizeBytes) {
            this.fileSizeBytes = fileSizeBytes;
            return this;
        }
        public Builder uploadedBy(User uploadedBy) {
            this.uploadedBy = uploadedBy;
            return this;
        }
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        public FileAttachment build() {
            return new FileAttachment(this.fileId, this.storagePath, this.originalFilename, this.fileType, this.mimeType, this.fileSizeBytes, this.uploadedBy, this.createdAt);
        }
    }

}

