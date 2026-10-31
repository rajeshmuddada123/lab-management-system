package com.college.resource.entity;

import com.college.resource.enums.ApprovalAction;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "booking_approvals")
public class BookingApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private Long approvalId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by", nullable = false)
    private User approvedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 10)
    private ApprovalAction action;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "actioned_at", nullable = false, updatable = false)
    private LocalDateTime actionedAt;

@PrePersist
    protected void onCreate() {
        actionedAt = LocalDateTime.now();
    }

    public BookingApproval() {
    }

    public BookingApproval(Long approvalId, Booking booking, User approvedBy, ApprovalAction action, String notes, LocalDateTime actionedAt) {
        this.approvalId = approvalId;
        this.booking = booking;
        this.approvedBy = approvedBy;
        this.action = action;
        this.notes = notes;
        this.actionedAt = actionedAt;
    }

    public Long getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public ApprovalAction getAction() {
        return action;
    }

    public void setAction(ApprovalAction action) {
        this.action = action;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getActionedAt() {
        return actionedAt;
    }

    public void setActionedAt(LocalDateTime actionedAt) {
        this.actionedAt = actionedAt;
    }

    public static  Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long approvalId;
        private Booking booking;
        private User approvedBy;
        private ApprovalAction action;
        private String notes;
        private LocalDateTime actionedAt;
        public Builder approvalId(Long approvalId) {
            this.approvalId = approvalId;
            return this;
        }
        public Builder booking(Booking booking) {
            this.booking = booking;
            return this;
        }
        public Builder approvedBy(User approvedBy) {
            this.approvedBy = approvedBy;
            return this;
        }
        public Builder action(ApprovalAction action) {
            this.action = action;
            return this;
        }
        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }
        public Builder actionedAt(LocalDateTime actionedAt) {
            this.actionedAt = actionedAt;
            return this;
        }
        public BookingApproval build() {
            return new BookingApproval(this.approvalId, this.booking, this.approvedBy, this.action, this.notes, this.actionedAt);
        }
    }

}

