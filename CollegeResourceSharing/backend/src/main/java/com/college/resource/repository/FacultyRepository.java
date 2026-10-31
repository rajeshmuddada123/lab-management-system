package com.college.resource.repository;

import com.college.resource.entity.Faculty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByUserUserId(Long userId);
    Optional<Faculty> findByFacultyCode(String facultyCode);
    boolean existsByFacultyCode(String facultyCode);

    @Query("SELECT f FROM Faculty f WHERE " +
           "LOWER(f.firstName) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(f.lastName) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(f.facultyCode) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(f.department) LIKE LOWER(CONCAT('%',:q,'%'))")
    Page<Faculty> searchFaculty(@Param("q") String keyword, Pageable pageable);
}
