package com.college.resource.entity;

import com.college.resource.enums.BookingStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(name = "purpose", nullable = false, columnDefinition = "TEXT")
    private String purpose;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookingStatus status;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "quantity_booked", nullable = false)
    private Integer quantityBooked;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

@PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = BookingStatus.PENDING;
        if (quantityBooked == null) quantityBooked = 1;
    }
@PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Booking() {
    }

    public Booking(Long bookingId, Student student, Equipment equipment, String purpose, LocalDate bookingDate, LocalTime startTime, LocalTime endTime, BookingStatus status, String rejectionReason, Integer quantityBooked, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.bookingId = bookingId;
        this.student = student;
        this.equipment = equipment;
        this.purpose = purpose;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.quantityBooked = quantityBooked;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
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

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Integer getQuantityBooked() {
        return quantityBooked;
    }

    public void setQuantityBooked(Integer quantityBooked) {
        this.quantityBooked = quantityBooked;
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
        private Long bookingId;
        private Student student;
        private Equipment equipment;
        private String purpose;
        private LocalDate bookingDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private BookingStatus status;
        private String rejectionReason;
        private Integer quantityBooked;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        public Builder bookingId(Long bookingId) {
            this.bookingId = bookingId;
            return this;
        }
        public Builder student(Student student) {
            this.student = student;
            return this;
        }
        public Builder equipment(Equipment equipment) {
            this.equipment = equipment;
            return this;
        }
        public Builder purpose(String purpose) {
            this.purpose = purpose;
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
        public Builder status(BookingStatus status) {
            this.status = status;
            return this;
        }
        public Builder rejectionReason(String rejectionReason) {
            this.rejectionReason = rejectionReason;
            return this;
        }
        public Builder quantityBooked(Integer quantityBooked) {
            this.quantityBooked = quantityBooked;
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
        public Booking build() {
            return new Booking(this.bookingId, this.student, this.equipment, this.purpose, this.bookingDate, this.startTime, this.endTime, this.status, this.rejectionReason, this.quantityBooked, this.createdAt, this.updatedAt);
        }
    }

}

