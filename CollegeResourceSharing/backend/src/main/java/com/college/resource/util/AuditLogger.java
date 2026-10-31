package com.college.resource.util;

import com.college.resource.entity.ActivityLog;
import com.college.resource.entity.User;
import com.college.resource.repository.ActivityLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AuditLogger {

    private final ActivityLogRepository activityLogRepository;

    public AuditLogger(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Async
    public void log(User user, String actionType, String entityType, Long entityId,
                    String description, String ip) {
        ActivityLog log = ActivityLog.builder()
                .user(user)
                .actionType(actionType)
                .entityType(entityType)
                .entityId(entityId)
                .description(description)
                .ipAddress(ip)
                .build();
        activityLogRepository.save(log);
    }

    @Async
    public void log(User user, String actionType, String entityType, Long entityId, String description) {
        log(user, actionType, entityType, entityId, description, null);
    }
}
