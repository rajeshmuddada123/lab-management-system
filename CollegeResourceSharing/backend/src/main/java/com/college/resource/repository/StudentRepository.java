package com.college.resource.repository;

import com.college.resource.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUserUserId(Long userId);
    Optional<Student> findByRegistrationNumber(String registrationNumber);
    boolean existsByRegistrationNumber(String registrationNumber);

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(s.registrationNumber) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(s.department) LIKE LOWER(CONCAT('%',:q,'%'))")
    Page<Student> searchStudents(@Param("q") String keyword, Pageable pageable);

    Page<Student> findByDepartment(String department, Pageable pageable);
}
