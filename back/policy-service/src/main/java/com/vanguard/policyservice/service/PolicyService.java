package com.vanguard.policyservice.service;

import com.vanguard.policyservice.dto.CoverageValidationRequest;
import com.vanguard.policyservice.dto.CoverageValidationResponse;
import com.vanguard.policyservice.dto.CreatePolicyRequest;
import com.vanguard.policyservice.dto.PolicyDto;
import com.vanguard.policyservice.entity.Policy;
import com.vanguard.policyservice.entity.PolicyStatus;
import com.vanguard.policyservice.repository.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;

    public PolicyService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Transactional
    public PolicyDto createPolicy(CreatePolicyRequest request) {
        Policy policy = new Policy();
        policy.setPolicyNumber(UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        policy.setHolderUserId(request.getHolderUserId());
        policy.setHolderUsername(request.getHolderUsername());
        policy.setPolicyType(request.getPolicyType());
        policy.setCoverageLimit(request.getCoverageLimit());
        policy.setDeductible(request.getDeductible());
        policy.setPremium(request.getPremium());
        policy.setStartDate(request.getStartDate());
        policy.setEndDate(request.getEndDate());
        policy.setStatus(request.getStatus());

        Policy saved = policyRepository.save(policy);
        return mapToDto(saved);
    }

    public List<PolicyDto> getAllPolicies() {
        return policyRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<PolicyDto> getPoliciesByHolder(String holderUserId) {
        return policyRepository.findByHolderUserId(holderUserId).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public PolicyDto getPolicyByNumber(String policyNumber) {
        return policyRepository.findByPolicyNumber(policyNumber)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
    }

    public Policy getPolicyEntityByNumber(String policyNumber) {
        return policyRepository.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
    }

    public CoverageValidationResponse validateCoverage(String policyNumber, CoverageValidationRequest request) {
        Policy policy = policyRepository.findByPolicyNumber(policyNumber)
                .orElse(null);

        if (policy == null) {
            return new CoverageValidationResponse(false, "Policy not found", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        if (policy.getStatus() != PolicyStatus.ACTIVE) {
            return new CoverageValidationResponse(false, "Policy is not ACTIVE (Status: " + policy.getStatus() + ")", policy.getCoverageLimit(), getRemaining(policy), policy.getDeductible());
        }

        if (request.getIncidentDate().isBefore(policy.getStartDate()) || request.getIncidentDate().isAfter(policy.getEndDate())) {
            return new CoverageValidationResponse(false, "Incident date is outside policy active dates", policy.getCoverageLimit(), getRemaining(policy), policy.getDeductible());
        }

        if (request.getClaimedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return new CoverageValidationResponse(false, "Claimed amount must be positive", policy.getCoverageLimit(), getRemaining(policy), policy.getDeductible());
        }

        BigDecimal remaining = getRemaining(policy);
        if (request.getClaimedAmount().compareTo(remaining) > 0) {
            return new CoverageValidationResponse(false, "Claimed amount exceeds remaining coverage", policy.getCoverageLimit(), remaining, policy.getDeductible());
        }

        return new CoverageValidationResponse(true, "Valid", policy.getCoverageLimit(), remaining, policy.getDeductible());
    }

    private BigDecimal getRemaining(Policy policy) {
        BigDecimal remaining = policy.getCoverageLimit().subtract(policy.getTotalClaimed());
        return remaining.compareTo(BigDecimal.ZERO) > 0 ? remaining : BigDecimal.ZERO;
    }

    private PolicyDto mapToDto(Policy policy) {
        PolicyDto dto = new PolicyDto();
        dto.setId(policy.getId());
        dto.setPolicyNumber(policy.getPolicyNumber());
        dto.setHolderUserId(policy.getHolderUserId());
        dto.setHolderUsername(policy.getHolderUsername());
        dto.setPolicyType(policy.getPolicyType());
        dto.setCoverageLimit(policy.getCoverageLimit());
        dto.setDeductible(policy.getDeductible());
        dto.setPremium(policy.getPremium());
        dto.setStartDate(policy.getStartDate());
        dto.setEndDate(policy.getEndDate());
        dto.setStatus(policy.getStatus());
        dto.setTotalClaimed(policy.getTotalClaimed());
        return dto;
    }
}
