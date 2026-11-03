package com.vanguard.settlementservice.repository;

import com.vanguard.settlementservice.entity.SettlementSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface SettlementSequenceRepository extends JpaRepository<SettlementSequence, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SettlementSequence> findById(String id);
}
