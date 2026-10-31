package com.college.resource.repository;

import com.college.resource.entity.Booking;
import com.college.resource.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByStudentStudentId(Long studentId, Pageable pageable);
    Page<Booking> findByEquipmentEquipmentId(Long equipmentId, Pageable pageable);
    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);

    // Booking conflict detection query — core concurrency guard
    @Query("SELECT COUNT(b) FROM Booking b WHERE " +
           "b.equipment.equipmentId = :equipmentId AND " +
           "b.bookingDate = :date AND " +
           "b.status NOT IN ('CANCELLED','REJECTED','NO_SHOW') AND " +
           "b.startTime < :endTime AND b.endTime > :startTime")
    long countConflicts(@Param("equipmentId") Long equipmentId,
                        @Param("date") LocalDate date,
                        @Param("startTime") LocalTime startTime,
                        @Param("endTime") LocalTime endTime);

    // Count approved/pending bookings for a student (for concurrent limit check)
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.student.studentId = :studentId " +
           "AND b.status IN ('PENDING','APPROVED','COLLECTED')")
    long countActiveBookingsForStudent(@Param("studentId") Long studentId);

    // Overdue bookings: approved+collected past their booking date
    @Query("SELECT b FROM Booking b WHERE b.status = 'COLLECTED' AND b.bookingDate < :today")
    List<Booking> findOverdue(@Param("today") LocalDate today);

    // Bookings for faculty's labs (for faculty approval)
    @Query("SELECT b FROM Booking b WHERE b.equipment.laboratory.labId IN :labIds " +
           "AND b.status = 'PENDING'")
    Page<Booking> findPendingForLabs(@Param("labIds") List<Long> labIds, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.equipment.laboratory.labId IN :labIds")
    Page<Booking> findAllForLabs(@Param("labIds") List<Long> labIds, Pageable pageable);

    Page<Booking> findByStudentStudentIdAndStatus(Long studentId, BookingStatus status, Pageable pageable);
}
