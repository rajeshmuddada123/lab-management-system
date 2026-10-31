package com.college.resource.repository;

import com.college.resource.entity.FacultyLabAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface FacultyLabAssignmentRepository extends JpaRepository<FacultyLabAssignment, Long> {
    List<FacultyLabAssignment> findByFacultyFacultyId(Long facultyId);
    List<FacultyLabAssignment> findByLaboratoryLabId(Long labId);
    Optional<FacultyLabAssignment> findByFacultyFacultyIdAndLaboratoryLabId(Long facultyId, Long labId);
    boolean existsByFacultyFacultyIdAndLaboratoryLabId(Long facultyId, Long labId);

    @Query("SELECT fla FROM FacultyLabAssignment fla WHERE fla.faculty.facultyId = :facultyId AND fla.endDate IS NULL")
    List<FacultyLabAssignment> findActiveByFacultyId(@Param("facultyId") Long facultyId);

    @Query("SELECT CASE WHEN COUNT(fla) > 0 THEN true ELSE false END FROM FacultyLabAssignment fla " +
           "WHERE fla.faculty.facultyId = :facultyId AND fla.laboratory.labId = :labId AND fla.endDate IS NULL")
    boolean isFacultyAssignedToLab(@Param("facultyId") Long facultyId, @Param("labId") Long labId);
}
