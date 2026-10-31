package com.college.resource.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "return_records")
public class ReturnRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "return_id")
    private Long returnId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "returned_by", nullable = false)
    private User returnedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by", nullable = false)
    private User receivedBy;

    @Column(name = "returned_at", nullable = false)
    private LocalDateTime returnedAt;

    @Column(name = "condition_on_return", nullable = false, length = 20)
    private String conditionOnReturn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_file_id")
    private FileAttachment photo;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

@PrePersist
    protected void onCreate() {
        if (returnedAt == null) returnedAt = LocalDateTime.now();
    }

    public ReturnRecord() {
    }

    public ReturnRecord(Long returnId, Booking booking, User returnedBy, User receivedBy, LocalDateTime returnedAt, String conditionOnReturn, FileAttachment photo, String notes) {
        this.returnId = returnId;
        this.booking = booking;
        this.returnedBy = returnedBy;
        this.receivedBy = receivedBy;
        this.returnedAt = returnedAt;
        this.conditionOnReturn = conditionOnReturn;
        this.photo = photo;
        this.notes = notes;
    }

    public Long getReturnId() {
        return returnId;
    }

    public void setReturnId(Long returnId) {
        this.returnId = returnId;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public User getReturnedBy() {
        return returnedBy;
    }

    public void setReturnedBy(User returnedBy) {
        this.returnedBy = returnedBy;
    }

    public User getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(User receivedBy) {
        this.receivedBy = receivedBy;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(LocalDateTime returnedAt) {
        this.returnedAt = returnedAt;
    }

    public String getConditionOnReturn() {
        return conditionOnReturn;
    }

    public void setConditionOnReturn(String conditionOnReturn) {
        this.conditionOnReturn = conditionOnReturn;
    }

    public FileAttachment getPhoto() {
        return photo;
    }

    public void setPhoto(FileAttachment photo) {
        this.photo = photo;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public static  Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long returnId;
        private Booking booking;
        private User returnedBy;
        private User receivedBy;
        private LocalDateTime returnedAt;
        private String conditionOnReturn;
        private FileAttachment photo;
        private String notes;
        public Builder returnId(Long returnId) {
            this.returnId = returnId;
            return this;
        }
        public Builder booking(Booking booking) {
            this.booking = booking;
            return this;
        }
        public Builder returnedBy(User returnedBy) {
            this.returnedBy = returnedBy;
            return this;
        }
        public Builder receivedBy(User receivedBy) {
            this.receivedBy = receivedBy;
            return this;
        }
        public Builder returnedAt(LocalDateTime returnedAt) {
            this.returnedAt = returnedAt;
            return this;
        }
        public Builder conditionOnReturn(String conditionOnReturn) {
            this.conditionOnReturn = conditionOnReturn;
            return this;
        }
        public Builder photo(FileAttachment photo) {
            this.photo = photo;
            return this;
        }
        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }
        public ReturnRecord build() {
            return new ReturnRecord(this.returnId, this.booking, this.returnedBy, this.receivedBy, this.returnedAt, this.conditionOnReturn, this.photo, this.notes);
        }
    }

}

