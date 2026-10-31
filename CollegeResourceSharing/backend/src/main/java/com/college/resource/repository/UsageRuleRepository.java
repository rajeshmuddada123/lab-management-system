package com.college.resource.repository;

import com.college.resource.entity.UsageRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsageRuleRepository extends JpaRepository<UsageRule, Long> {
    Optional<UsageRule> findByLaboratoryLabId(Long labId);
}
