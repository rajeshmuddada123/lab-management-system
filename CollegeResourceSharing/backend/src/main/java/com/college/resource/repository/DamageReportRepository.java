package com.college.resource.repository;

import com.college.resource.entity.DamageReport;
import com.college.resource.enums.DamageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DamageReportRepository extends JpaRepository<DamageReport, Long> {
    Page<DamageReport> findByEquipmentEquipmentId(Long equipmentId, Pageable pageable);
    Page<DamageReport> findByStatus(DamageStatus status, Pageable pageable);
    List<DamageReport> findByStatus(DamageStatus status);
}
