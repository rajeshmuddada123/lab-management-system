package com.college.resource.entity;

import com.college.resource.enums.NotificationType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private NotificationType type;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_booking_id")
    private Booking relatedBooking;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

@PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isRead == null) isRead = false;
    }

    public Notification() {
    }

    public Notification(Long notificationId, User user, String title, String message, NotificationType type, Boolean isRead, Booking relatedBooking, LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.relatedBooking = relatedBooking;
        this.createdAt = createdAt;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Boolean isIsRead() {
        return isRead;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Booking getRelatedBooking() {
        return relatedBooking;
    }

    public void setRelatedBooking(Booking relatedBooking) {
        this.relatedBooking = relatedBooking;
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
        private Long notificationId;
        private User user;
        private String title;
        private String message;
        private NotificationType type;
        private Boolean isRead;
        private Booking relatedBooking;
        private LocalDateTime createdAt;
        public Builder notificationId(Long notificationId) {
            this.notificationId = notificationId;
            return this;
        }
        public Builder user(User user) {
            this.user = user;
            return this;
        }
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        public Builder type(NotificationType type) {
            this.type = type;
            return this;
        }
        public Builder isRead(Boolean isRead) {
            this.isRead = isRead;
            return this;
        }
        public Builder relatedBooking(Booking relatedBooking) {
            this.relatedBooking = relatedBooking;
            return this;
        }
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        public Notification build() {
            return new Notification(this.notificationId, this.user, this.title, this.message, this.type, this.isRead, this.relatedBooking, this.createdAt);
        }
    }

}

