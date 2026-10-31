package com.college.resource.service;

import com.college.resource.entity.*;
import com.college.resource.enums.NotificationType;
import com.college.resource.repository.NotificationRepository;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Async
    @Transactional
    public void notifyUser(User user, String title, String message,
                           NotificationType type, Booking relatedBooking) {
        Notification n = Notification.builder()
                .user(user).title(title).message(message)
                .type(type).relatedBooking(relatedBooking).build();
        notificationRepository.save(n);
    }

    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAllRead(Long userId) {
        notificationRepository.markAllReadForUser(userId);
    }

    @Transactional
    public void markOneRead(Long notificationId, Long userId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            if (n.getUser().getUserId().equals(userId)) {
                n.setIsRead(true);
                notificationRepository.save(n);
            }
        });
    }
}
