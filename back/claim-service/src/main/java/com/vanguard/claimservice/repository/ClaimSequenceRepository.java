package com.vanguard.claimservice.repository;

import com.vanguard.claimservice.entity.ClaimSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface ClaimSequenceRepository extends JpaRepository<ClaimSequence, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ClaimSequence> findById(String id);
}
