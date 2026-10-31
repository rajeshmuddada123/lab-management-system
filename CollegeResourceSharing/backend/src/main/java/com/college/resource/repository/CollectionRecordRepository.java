package com.college.resource.repository;

import com.college.resource.entity.CollectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CollectionRecordRepository extends JpaRepository<CollectionRecord, Long> {
    Optional<CollectionRecord> findByBookingBookingId(Long bookingId);
    boolean existsByBookingBookingId(Long bookingId);
}
