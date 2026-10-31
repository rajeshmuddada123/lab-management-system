package com.college.resource.repository;

import com.college.resource.entity.Equipment;
import com.college.resource.enums.EquipmentCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    Optional<Equipment> findByEquipmentCodeAndIsDeletedFalse(String code);
    boolean existsByEquipmentCode(String code);

    Page<Equipment> findByIsDeletedFalse(Pageable pageable);
    Page<Equipment> findByLaboratoryLabIdAndIsDeletedFalse(Long labId, Pageable pageable);
    Page<Equipment> findByCategoryAndIsDeletedFalse(String category, Pageable pageable);

    @Query("SELECT e FROM Equipment e WHERE e.isDeleted = false AND e.quantityAvailable > 0")
    Page<Equipment> findAvailable(Pageable pageable);

    @Query("SELECT e FROM Equipment e WHERE e.isDeleted = false AND " +
           "(LOWER(e.name) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(e.category) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(e.equipmentCode) LIKE LOWER(CONCAT('%',:q,'%')))")
    Page<Equipment> searchEquipment(@Param("q") String keyword, Pageable pageable);

    @Query("SELECT e FROM Equipment e WHERE e.isDeleted = false AND e.laboratory.labId = :labId " +
           "AND (:category IS NULL OR e.category = :category) " +
           "AND (:available IS NULL OR (:available = true AND e.quantityAvailable > 0))")
    Page<Equipment> findByLabWithFilters(@Param("labId") Long labId,
                                          @Param("category") String category,
                                          @Param("available") Boolean available,
                                          Pageable pageable);

    List<Equipment> findByLaboratoryLabIdAndIsDeletedFalse(Long labId);
}
