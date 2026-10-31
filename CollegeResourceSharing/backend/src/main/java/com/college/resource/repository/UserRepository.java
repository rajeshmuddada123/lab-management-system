package com.college.resource.repository;

import com.college.resource.entity.User;
import com.college.resource.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndIsDeletedFalse(String username);
    Optional<User> findByEmailAndIsDeletedFalse(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<User> findByRoleRoleNameAndIsDeletedFalse(String roleName, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND u.role.roleName = :role " +
           "AND (LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<User> searchByRoleAndKeyword(@Param("role") String role,
                                       @Param("q") String keyword,
                                       Pageable pageable);
}
