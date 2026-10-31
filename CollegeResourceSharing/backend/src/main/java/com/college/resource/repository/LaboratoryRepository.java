package com.college.resource.repository;

import com.college.resource.entity.Laboratory;
import com.college.resource.enums.LabStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LaboratoryRepository extends JpaRepository<Laboratory, Long> {
    Optional<Laboratory> findByLabCode(String labCode);
    boolean existsByLabCode(String labCode);
    List<Laboratory> findByStatus(LabStatus status);
    Page<Laboratory> findByStatus(LabStatus status, Pageable pageable);
}
