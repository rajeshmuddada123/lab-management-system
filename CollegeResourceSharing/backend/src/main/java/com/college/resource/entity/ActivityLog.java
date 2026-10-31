package com.college.resource.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "action_type", nullable = false, length = 60)
    private String actionType;

    @Column(name = "entity_type", nullable = false, length = 60)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

@PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public ActivityLog() {
    }

    public ActivityLog(Long logId, User user, String actionType, String entityType, Long entityId, String description, String ipAddress, LocalDateTime createdAt) {
        this.logId = logId;
        this.user = user;
        this.actionType = actionType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.description = description;
        this.ipAddress = ipAddress;
        this.createdAt = createdAt;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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
        private Long logId;
        private User user;
        private String actionType;
        private String entityType;
        private Long entityId;
        private String description;
        private String ipAddress;
        private LocalDateTime createdAt;
        public Builder logId(Long logId) {
            this.logId = logId;
            return this;
        }
        public Builder user(User user) {
            this.user = user;
            return this;
        }
        public Builder actionType(String actionType) {
            this.actionType = actionType;
            return this;
        }
        public Builder entityType(String entityType) {
            this.entityType = entityType;
            return this;
        }
        public Builder entityId(Long entityId) {
            this.entityId = entityId;
            return this;
        }
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        public ActivityLog build() {
            return new ActivityLog(this.logId, this.user, this.actionType, this.entityType, this.entityId, this.description, this.ipAddress, this.createdAt);
        }
    }

}

