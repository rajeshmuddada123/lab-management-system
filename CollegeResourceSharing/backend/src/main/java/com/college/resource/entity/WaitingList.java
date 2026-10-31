package com.college.resource.entity;

import com.college.resource.enums.WaitlistStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "waiting_lists")
public class WaitingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waitlist_id")
    private Long waitlistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "queue_position", nullable = false)
    private Integer queuePosition;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private WaitlistStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

@PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = WaitlistStatus.WAITING;
    }
@PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public WaitingList() {
    }

    public WaitingList(Long waitlistId, Equipment equipment, Student student, LocalDate bookingDate, LocalTime startTime, LocalTime endTime, Integer queuePosition, WaitlistStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.waitlistId = waitlistId;
        this.equipment = equipment;
        this.student = student;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.queuePosition = queuePosition;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getWaitlistId() {
        return waitlistId;
    }

    public void setWaitlistId(Long waitlistId) {
        this.waitlistId = waitlistId;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(Integer queuePosition) {
        this.queuePosition = queuePosition;
    }

    public WaitlistStatus getStatus() {
        return status;
    }

    public void setStatus(WaitlistStatus status) {
        this.status = status;
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
        private Long waitlistId;
        private Equipment equipment;
        private Student student;
        private LocalDate bookingDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private Integer queuePosition;
        private WaitlistStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        public Builder waitlistId(Long waitlistId) {
            this.waitlistId = waitlistId;
            return this;
        }
        public Builder equipment(Equipment equipment) {
            this.equipment = equipment;
            return this;
        }
        public Builder student(Student student) {
            this.student = student;
            return this;
        }
        public Builder bookingDate(LocalDate bookingDate) {
            this.bookingDate = bookingDate;
            return this;
        }
        public Builder startTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }
        public Builder endTime(LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }
        public Builder queuePosition(Integer queuePosition) {
            this.queuePosition = queuePosition;
            return this;
        }
        public Builder status(WaitlistStatus status) {
            this.status = status;
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
        public WaitingList build() {
            return new WaitingList(this.waitlistId, this.equipment, this.student, this.bookingDate, this.startTime, this.endTime, this.queuePosition, this.status, this.createdAt, this.updatedAt);
        }
    }

}

