package com.college.resource.entity;

import com.college.resource.enums.DamageSeverity;
import com.college.resource.enums.DamageStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "damage_reports")
public class DamageReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "damage_id")
    private Long damageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by", nullable = false)
    private User reportedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 15)
    private DamageSeverity severity;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "repair_cost", precision = 10, scale = 2)
    private BigDecimal repairCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 25)
    private DamageStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_file_id")
    private FileAttachment photo;

    @Column(name = "reported_at", nullable = false, updatable = false)
    private LocalDateTime reportedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

@PrePersist
    protected void onCreate() {
        reportedAt = LocalDateTime.now();
        if (status == null) status = DamageStatus.REPORTED;
    }

    public DamageReport() {
    }

    public DamageReport(Long damageId, Equipment equipment, Booking booking, User reportedBy, DamageSeverity severity, String description, BigDecimal repairCost, DamageStatus status, FileAttachment photo, LocalDateTime reportedAt, LocalDateTime resolvedAt) {
        this.damageId = damageId;
        this.equipment = equipment;
        this.booking = booking;
        this.reportedBy = reportedBy;
        this.severity = severity;
        this.description = description;
        this.repairCost = repairCost;
        this.status = status;
        this.photo = photo;
        this.reportedAt = reportedAt;
        this.resolvedAt = resolvedAt;
    }

    public Long getDamageId() {
        return damageId;
    }

    public void setDamageId(Long damageId) {
        this.damageId = damageId;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public User getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(User reportedBy) {
        this.reportedBy = reportedBy;
    }

    public DamageSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(DamageSeverity severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(BigDecimal repairCost) {
        this.repairCost = repairCost;
    }

    public DamageStatus getStatus() {
        return status;
    }

    public void setStatus(DamageStatus status) {
        this.status = status;
    }

    public FileAttachment getPhoto() {
        return photo;
    }

    public void setPhoto(FileAttachment photo) {
        this.photo = photo;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(LocalDateTime reportedAt) {
        this.reportedAt = reportedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public static  Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long damageId;
        private Equipment equipment;
        private Booking booking;
        private User reportedBy;
        private DamageSeverity severity;
        private String description;
        private BigDecimal repairCost;
        private DamageStatus status;
        private FileAttachment photo;
        private LocalDateTime reportedAt;
        private LocalDateTime resolvedAt;
        public Builder damageId(Long damageId) {
            this.damageId = damageId;
            return this;
        }
        public Builder equipment(Equipment equipment) {
            this.equipment = equipment;
            return this;
        }
        public Builder booking(Booking booking) {
            this.booking = booking;
            return this;
        }
        public Builder reportedBy(User reportedBy) {
            this.reportedBy = reportedBy;
            return this;
        }
        public Builder severity(DamageSeverity severity) {
            this.severity = severity;
            return this;
        }
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        public Builder repairCost(BigDecimal repairCost) {
            this.repairCost = repairCost;
            return this;
        }
        public Builder status(DamageStatus status) {
            this.status = status;
            return this;
        }
        public Builder photo(FileAttachment photo) {
            this.photo = photo;
            return this;
        }
        public Builder reportedAt(LocalDateTime reportedAt) {
            this.reportedAt = reportedAt;
            return this;
        }
        public Builder resolvedAt(LocalDateTime resolvedAt) {
            this.resolvedAt = resolvedAt;
            return this;
        }
        public DamageReport build() {
            return new DamageReport(this.damageId, this.equipment, this.booking, this.reportedBy, this.severity, this.description, this.repairCost, this.status, this.photo, this.reportedAt, this.resolvedAt);
        }
    }

}

