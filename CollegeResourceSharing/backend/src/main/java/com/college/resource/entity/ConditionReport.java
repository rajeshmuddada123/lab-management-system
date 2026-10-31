package com.college.resource.entity;

import com.college.resource.enums.ConditionStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "condition_reports")
public class ConditionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by", nullable = false)
    private User reportedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_status", nullable = false, length = 20)
    private ConditionStatus conditionStatus;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_file_id")
    private FileAttachment photo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

@PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (reportDate == null) reportDate = LocalDate.now();
    }

    public ConditionReport() {
    }

    public ConditionReport(Long reportId, Equipment equipment, User reportedBy, ConditionStatus conditionStatus, String description, LocalDate reportDate, FileAttachment photo, LocalDateTime createdAt) {
        this.reportId = reportId;
        this.equipment = equipment;
        this.reportedBy = reportedBy;
        this.conditionStatus = conditionStatus;
        this.description = description;
        this.reportDate = reportDate;
        this.photo = photo;
        this.createdAt = createdAt;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public User getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(User reportedBy) {
        this.reportedBy = reportedBy;
    }

    public ConditionStatus getConditionStatus() {
        return conditionStatus;
    }

    public void setConditionStatus(ConditionStatus conditionStatus) {
        this.conditionStatus = conditionStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public FileAttachment getPhoto() {
        return photo;
    }

    public void setPhoto(FileAttachment photo) {
        this.photo = photo;
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
        private Long reportId;
        private Equipment equipment;
        private User reportedBy;
        private ConditionStatus conditionStatus;
        private String description;
        private LocalDate reportDate;
        private FileAttachment photo;
        private LocalDateTime createdAt;
        public Builder reportId(Long reportId) {
            this.reportId = reportId;
            return this;
        }
        public Builder equipment(Equipment equipment) {
            this.equipment = equipment;
            return this;
        }
        public Builder reportedBy(User reportedBy) {
            this.reportedBy = reportedBy;
            return this;
        }
        public Builder conditionStatus(ConditionStatus conditionStatus) {
            this.conditionStatus = conditionStatus;
            return this;
        }
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        public Builder reportDate(LocalDate reportDate) {
            this.reportDate = reportDate;
            return this;
        }
        public Builder photo(FileAttachment photo) {
            this.photo = photo;
            return this;
        }
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        public ConditionReport build() {
            return new ConditionReport(this.reportId, this.equipment, this.reportedBy, this.conditionStatus, this.description, this.reportDate, this.photo, this.createdAt);
        }
    }

}

