package com.college.resource.repository;

import com.college.resource.entity.BookingApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BookingApprovalRepository extends JpaRepository<BookingApproval, Long> {
    Optional<BookingApproval> findByBookingBookingId(Long bookingId);
    boolean existsByBookingBookingId(Long bookingId);
}
