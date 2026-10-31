package com.college.resource.repository;

import com.college.resource.entity.ReturnRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReturnRecordRepository extends JpaRepository<ReturnRecord, Long> {
    Optional<ReturnRecord> findByBookingBookingId(Long bookingId);
    boolean existsByBookingBookingId(Long bookingId);
}
