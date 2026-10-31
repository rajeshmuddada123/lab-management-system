package com.college.resource.entity;

import com.college.resource.enums.LabStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "laboratories")
public class Laboratory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lab_id")
    private Long labId;

    @Column(name = "lab_code", nullable = false, unique = true, length = 20)
    private String labCode;

    @Column(name = "lab_name", nullable = false, length = 150)
    private String labName;

    @Column(name = "location", nullable = false, length = 200)
    private String location;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LabStatus status;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

@PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = LabStatus.ACTIVE;
    }
@PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Laboratory() {
    }

    public Laboratory(Long labId, String labCode, String labName, String location, Integer capacity, LabStatus status, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.labId = labId;
        this.labCode = labCode;
        this.labName = labName;
        this.location = location;
        this.capacity = capacity;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getLabId() {
        return labId;
    }

    public void setLabId(Long labId) {
        this.labId = labId;
    }

    public String getLabCode() {
        return labCode;
    }

    public void setLabCode(String labCode) {
        this.labCode = labCode;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public LabStatus getStatus() {
        return status;
    }

    public void setStatus(LabStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static  Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long labId;
        private String labCode;
        private String labName;
        private String location;
        private Integer capacity;
        private LabStatus status;
        private String description;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        public Builder labId(Long labId) {
            this.labId = labId;
            return this;
        }
        public Builder labCode(String labCode) {
            this.labCode = labCode;
            return this;
        }
        public Builder labName(String labName) {
            this.labName = labName;
            return this;
        }
        public Builder location(String location) {
            this.location = location;
            return this;
        }
        public Builder capacity(Integer capacity) {
            this.capacity = capacity;
            return this;
        }
        public Builder status(LabStatus status) {
            this.status = status;
            return this;
        }
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        public Laboratory build() {
            return new Laboratory(this.labId, this.labCode, this.labName, this.location, this.capacity, this.status, this.description, this.createdAt, this.updatedAt);
        }
    }

}

