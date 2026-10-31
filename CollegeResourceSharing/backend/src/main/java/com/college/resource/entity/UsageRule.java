package com.college.resource.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "usage_rules")
public class UsageRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_id")
    private Long ruleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id", nullable = false)
    private Laboratory laboratory;

    @Column(name = "max_booking_duration_hours", nullable = false)
    private Integer maxBookingDurationHours;

    @Column(name = "max_advance_booking_days", nullable = false)
    private Integer maxAdvanceBookingDays;

    @Column(name = "max_concurrent_bookings_per_student", nullable = false)
    private Integer maxConcurrentBookingsPerStudent;

    @Column(name = "allowed_days", nullable = false, length = 50)
    private String allowedDays;

    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

@PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
@PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UsageRule() {
    }

    public UsageRule(Long ruleId, Laboratory laboratory, Integer maxBookingDurationHours, Integer maxAdvanceBookingDays, Integer maxConcurrentBookingsPerStudent, String allowedDays, LocalTime openTime, LocalTime closeTime, String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.ruleId = ruleId;
        this.laboratory = laboratory;
        this.maxBookingDurationHours = maxBookingDurationHours;
        this.maxAdvanceBookingDays = maxAdvanceBookingDays;
        this.maxConcurrentBookingsPerStudent = maxConcurrentBookingsPerStudent;
        this.allowedDays = allowedDays;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Laboratory getLaboratory() {
        return laboratory;
    }

    public void setLaboratory(Laboratory laboratory) {
        this.laboratory = laboratory;
    }

    public Integer getMaxBookingDurationHours() {
        return maxBookingDurationHours;
    }

    public void setMaxBookingDurationHours(Integer maxBookingDurationHours) {
        this.maxBookingDurationHours = maxBookingDurationHours;
    }

    public Integer getMaxAdvanceBookingDays() {
        return maxAdvanceBookingDays;
    }

    public void setMaxAdvanceBookingDays(Integer maxAdvanceBookingDays) {
        this.maxAdvanceBookingDays = maxAdvanceBookingDays;
    }

    public Integer getMaxConcurrentBookingsPerStudent() {
        return maxConcurrentBookingsPerStudent;
    }

    public void setMaxConcurrentBookingsPerStudent(Integer maxConcurrentBookingsPerStudent) {
        this.maxConcurrentBookingsPerStudent = maxConcurrentBookingsPerStudent;
    }

    public String getAllowedDays() {
        return allowedDays;
    }

    public void setAllowedDays(String allowedDays) {
        this.allowedDays = allowedDays;
    }

    public LocalTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public LocalTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
        private Long ruleId;
        private Laboratory laboratory;
        private Integer maxBookingDurationHours;
        private Integer maxAdvanceBookingDays;
        private Integer maxConcurrentBookingsPerStudent;
        private String allowedDays;
        private LocalTime openTime;
        private LocalTime closeTime;
        private String notes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        public Builder ruleId(Long ruleId) {
            this.ruleId = ruleId;
            return this;
        }
        public Builder laboratory(Laboratory laboratory) {
            this.laboratory = laboratory;
            return this;
        }
        public Builder maxBookingDurationHours(Integer maxBookingDurationHours) {
            this.maxBookingDurationHours = maxBookingDurationHours;
            return this;
        }
        public Builder maxAdvanceBookingDays(Integer maxAdvanceBookingDays) {
            this.maxAdvanceBookingDays = maxAdvanceBookingDays;
            return this;
        }
        public Builder maxConcurrentBookingsPerStudent(Integer maxConcurrentBookingsPerStudent) {
            this.maxConcurrentBookingsPerStudent = maxConcurrentBookingsPerStudent;
            return this;
        }
        public Builder allowedDays(String allowedDays) {
            this.allowedDays = allowedDays;
            return this;
        }
        public Builder openTime(LocalTime openTime) {
            this.openTime = openTime;
            return this;
        }
        public Builder closeTime(LocalTime closeTime) {
            this.closeTime = closeTime;
            return this;
        }
        public Builder notes(String notes) {
            this.notes = notes;
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
        public UsageRule build() {
            return new UsageRule(this.ruleId, this.laboratory, this.maxBookingDurationHours, this.maxAdvanceBookingDays, this.maxConcurrentBookingsPerStudent, this.allowedDays, this.openTime, this.closeTime, this.notes, this.createdAt, this.updatedAt);
        }
    }

}

