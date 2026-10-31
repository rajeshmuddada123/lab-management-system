package com.college.resource.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "equipment_images")
public class EquipmentImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false, unique = true)
    private FileAttachment file;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

@PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isPrimary == null) isPrimary = false;
        if (displayOrder == null) displayOrder = 0;
    }

    public EquipmentImage() {
    }

    public EquipmentImage(Long imageId, Equipment equipment, FileAttachment file, Boolean isPrimary, Integer displayOrder, LocalDateTime createdAt) {
        this.imageId = imageId;
        this.equipment = equipment;
        this.file = file;
        this.isPrimary = isPrimary;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public FileAttachment getFile() {
        return file;
    }

    public void setFile(FileAttachment file) {
        this.file = file;
    }

    public Boolean isIsPrimary() {
        return isPrimary;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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
        private Long imageId;
        private Equipment equipment;
        private FileAttachment file;
        private Boolean isPrimary;
        private Integer displayOrder;
        private LocalDateTime createdAt;
        public Builder imageId(Long imageId) {
            this.imageId = imageId;
            return this;
        }
        public Builder equipment(Equipment equipment) {
            this.equipment = equipment;
            return this;
        }
        public Builder file(FileAttachment file) {
            this.file = file;
            return this;
        }
        public Builder isPrimary(Boolean isPrimary) {
            this.isPrimary = isPrimary;
            return this;
        }
        public Builder displayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
            return this;
        }
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        public EquipmentImage build() {
            return new EquipmentImage(this.imageId, this.equipment, this.file, this.isPrimary, this.displayOrder, this.createdAt);
        }
    }

}

