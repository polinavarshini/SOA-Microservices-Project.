package com.vanguard.settlementservice.repository;

import com.vanguard.settlementservice.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SettlementRepository extends JpaRepository<Settlement, UUID> {
    Optional<Settlement> findBySettlementNumber(String settlementNumber);
    Optional<Settlement> findByClaimNumber(String claimNumber);
    boolean existsByClaimNumber(String claimNumber);
}
