package com.college.resource.repository;

import com.college.resource.entity.ConditionReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConditionReportRepository extends JpaRepository<ConditionReport, Long> {
    Page<ConditionReport> findByEquipmentEquipmentId(Long equipmentId, Pageable pageable);
}
