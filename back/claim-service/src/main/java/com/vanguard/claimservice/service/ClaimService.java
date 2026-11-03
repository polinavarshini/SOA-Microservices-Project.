package com.vanguard.claimservice.service;

import com.vanguard.claimservice.client.PolicyServiceClient;
import com.vanguard.claimservice.client.SettlementServiceClient;
import com.vanguard.claimservice.dto.*;
import com.vanguard.claimservice.entity.Claim;
import com.vanguard.claimservice.entity.ClaimSequence;
import com.vanguard.claimservice.entity.ClaimStatus;
import com.vanguard.claimservice.exception.ServiceUnavailableException;
import com.vanguard.claimservice.repository.ClaimRepository;
import com.vanguard.claimservice.repository.ClaimSequenceRepository;
import com.vanguard.claimservice.security.CustomUserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimSequenceRepository sequenceRepository;
    private final PolicyServiceClient policyServiceClient;
    private final SettlementServiceClient settlementServiceClient;

    public ClaimService(ClaimRepository claimRepository, 
                        ClaimSequenceRepository sequenceRepository, 
                        PolicyServiceClient policyServiceClient,
                        SettlementServiceClient settlementServiceClient) {
        this.claimRepository = claimRepository;
        this.sequenceRepository = sequenceRepository;
        this.policyServiceClient = policyServiceClient;
        this.settlementServiceClient = settlementServiceClient;
    }

    @Transactional
    public String generateClaimNumber() {
        ClaimSequence seq = sequenceRepository.findById("SEQ").orElse(new ClaimSequence());
        seq.setNextVal(seq.getNextVal() + 1);
        sequenceRepository.save(seq);
        return "CLM" + seq.getNextVal();
    }

    @Transactional(noRollbackFor = ServiceUnavailableException.class)
    public ClaimDto submitClaim(SubmitClaimRequest request, CustomUserDetails user) {
        Claim claim = new Claim();
        claim.setClaimNumber(generateClaimNumber());
        claim.setPolicyNumber(request.getPolicyNumber());
        claim.setClaimantUserId(user.getUserId());
        claim.setClaimantUsername(user.getUsername());
        claim.setClaimedAmount(request.getClaimedAmount());
        claim.setIncidentDate(request.getIncidentDate());
        claim.setDescription(request.getDescription());
        claim.setStatus(ClaimStatus.SUBMITTED);

        claim = claimRepository.saveAndFlush(claim);
        return executeCoverageValidation(claim);
    }

    @Transactional
    public ClaimDto validateClaim(String claimNumber) {
        Claim claim = getClaimEntity(claimNumber);
        if (claim.getStatus() != ClaimStatus.SUBMITTED) {
            throw new RuntimeException("Claim must be in SUBMITTED state to validate");
        }
        return executeCoverageValidation(claim);
    }

    @CircuitBreaker(name = "policyService", fallbackMethod = "fallbackValidation")
    @Retry(name = "policyService", fallbackMethod = "fallbackValidation")
    private ClaimDto executeCoverageValidation(Claim claim) {
        CoverageValidationRequest req = new CoverageValidationRequest(claim.getClaimedAmount(), claim.getIncidentDate());
        CoverageValidationResponse res = policyServiceClient.validateCoverage(claim.getPolicyNumber(), req);

        if (res.isValid()) {
            claim.setStatus(ClaimStatus.VALIDATED);
        } else {
            claim.setStatus(ClaimStatus.REJECTED);
            claim.setRejectionReason(res.getReason());
        }
        return mapToDto(claimRepository.save(claim));
    }

    private ClaimDto fallbackValidation(Claim claim, Throwable t) {
        throw new ServiceUnavailableException("Policy Service is currently unavailable. Claim remains SUBMITTED.");
    }

    @Transactional(noRollbackFor = ServiceUnavailableException.class)
    public ClaimDto approveClaim(String claimNumber, ApproveClaimRequest request) {
        Claim claim = getClaimEntity(claimNumber);
        if (claim.getStatus() != ClaimStatus.VALIDATED) {
            throw new RuntimeException("Claim must be VALIDATED to approve");
        }
        if (request.getApprovedAmount().compareTo(claim.getClaimedAmount()) > 0) {
            throw new RuntimeException("Approved amount cannot exceed claimed amount");
        }
        
        claim.setApprovedAmount(request.getApprovedAmount());
        claim.setStatus(ClaimStatus.APPROVED);
        claim = claimRepository.saveAndFlush(claim);
        
        return executeSettlement(claim);
    }

    @CircuitBreaker(name = "settlementService", fallbackMethod = "fallbackSettlement")
    @Retry(name = "settlementService", fallbackMethod = "fallbackSettlement")
    private ClaimDto executeSettlement(Claim claim) {
        PolicyDto policy = policyServiceClient.getPolicy(claim.getPolicyNumber());
        
        ProcessSettlementRequest stlReq = new ProcessSettlementRequest(
                claim.getClaimNumber(), claim.getPolicyNumber(), claim.getApprovedAmount(), policy.getDeductible());
        
        SettlementDto stl = settlementServiceClient.processSettlement(stlReq);
        if ("COMPLETED".equals(stl.getStatus())) {
            claim.setStatus(ClaimStatus.SETTLED);
        } else {
            claim.setStatus(ClaimStatus.SETTLEMENT_FAILED);
        }
        return mapToDto(claimRepository.save(claim));
    }

    private ClaimDto fallbackSettlement(Claim claim, Throwable t) {
        throw new ServiceUnavailableException("Settlement Service is currently unavailable. Claim remains APPROVED.");
    }

    @Transactional
    public ClaimDto rejectClaim(String claimNumber, RejectClaimRequest request) {
        Claim claim = getClaimEntity(claimNumber);
        claim.setStatus(ClaimStatus.REJECTED);
        claim.setRejectionReason(request.getReason());
        return mapToDto(claimRepository.save(claim));
    }

    public ClaimDto getClaim(String claimNumber, CustomUserDetails user) {
        Claim claim = getClaimEntity(claimNumber);
        if ("POLICYHOLDER".equals(user.getRole()) && !claim.getClaimantUserId().equals(user.getUserId())) {
            throw new RuntimeException("Access Denied");
        }
        return mapToDto(claim);
    }

    public List<ClaimDto> getClaims(CustomUserDetails user) {
        if ("ADJUSTER".equals(user.getRole())) {
            return claimRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
        }
        return claimRepository.findByClaimantUserId(user.getUserId()).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private Claim getClaimEntity(String claimNumber) {
        return claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
    }

    private ClaimDto mapToDto(Claim claim) {
        ClaimDto dto = new ClaimDto();
        dto.setId(claim.getId());
        dto.setClaimNumber(claim.getClaimNumber());
        dto.setPolicyNumber(claim.getPolicyNumber());
        dto.setClaimantUserId(claim.getClaimantUserId());
        dto.setClaimantUsername(claim.getClaimantUsername());
        dto.setClaimedAmount(claim.getClaimedAmount());
        dto.setApprovedAmount(claim.getApprovedAmount());
        dto.setIncidentDate(claim.getIncidentDate());
        dto.setDescription(claim.getDescription());
        dto.setStatus(claim.getStatus());
        dto.setRejectionReason(claim.getRejectionReason());
        dto.setCreatedAt(claim.getCreatedAt());
        dto.setUpdatedAt(claim.getUpdatedAt());
        return dto;
    }
}
