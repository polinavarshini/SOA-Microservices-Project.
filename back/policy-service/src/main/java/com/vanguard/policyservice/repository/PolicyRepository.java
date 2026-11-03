package com.vanguard.policyservice.repository;

import com.vanguard.policyservice.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PolicyRepository extends JpaRepository<Policy, UUID> {
    Optional<Policy> findByPolicyNumber(String policyNumber);
    List<Policy> findByHolderUserId(String holderUserId);
}
