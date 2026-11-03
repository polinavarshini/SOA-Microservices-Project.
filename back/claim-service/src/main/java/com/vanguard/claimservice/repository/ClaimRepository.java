package com.vanguard.claimservice.repository;

import com.vanguard.claimservice.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClaimRepository extends JpaRepository<Claim, UUID> {
    Optional<Claim> findByClaimNumber(String claimNumber);
    List<Claim> findByClaimantUserId(String claimantUserId);
}
