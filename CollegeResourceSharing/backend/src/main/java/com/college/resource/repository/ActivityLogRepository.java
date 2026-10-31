package com.college.resource.repository;

import com.college.resource.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    Page<ActivityLog> findByUserUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<ActivityLog> findByActionTypeOrderByCreatedAtDesc(String actionType, Pageable pageable);
    Page<ActivityLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
