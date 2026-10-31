package com.college.resource.repository;

import com.college.resource.entity.WaitingList;
import com.college.resource.enums.WaitlistStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface WaitingListRepository extends JpaRepository<WaitingList, Long> {
    Page<WaitingList> findByStudentStudentId(Long studentId, Pageable pageable);
    List<WaitingList> findByEquipmentEquipmentIdAndStatusOrderByQueuePositionAsc(
        Long equipmentId, WaitlistStatus status);

    @Query("SELECT MAX(w.queuePosition) FROM WaitingList w WHERE " +
           "w.equipment.equipmentId = :equipmentId AND " +
           "w.bookingDate = :date AND w.startTime = :startTime AND w.endTime = :endTime")
    Integer findMaxQueuePosition(@Param("equipmentId") Long equipmentId,
                                  @Param("date") LocalDate date,
                                  @Param("startTime") LocalTime startTime,
                                  @Param("endTime") LocalTime endTime);

    List<WaitingList> findByEquipmentEquipmentIdAndBookingDateAndStartTimeAndEndTimeAndStatus(
        Long equipmentId, LocalDate date, LocalTime startTime, LocalTime endTime, WaitlistStatus status);
}
