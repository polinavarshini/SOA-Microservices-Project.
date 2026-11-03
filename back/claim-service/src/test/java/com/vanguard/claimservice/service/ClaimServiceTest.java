package com.vanguard.claimservice.service;

import com.vanguard.claimservice.client.PolicyServiceClient;
import com.vanguard.claimservice.client.SettlementServiceClient;
import com.vanguard.claimservice.dto.*;
import com.vanguard.claimservice.entity.Claim;
import com.vanguard.claimservice.entity.ClaimSequence;
import com.vanguard.claimservice.entity.ClaimStatus;
import com.vanguard.claimservice.repository.ClaimRepository;
import com.vanguard.claimservice.repository.ClaimSequenceRepository;
import com.vanguard.claimservice.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private ClaimSequenceRepository sequenceRepository;

    @Mock
    private PolicyServiceClient policyServiceClient;

    @Mock
    private SettlementServiceClient settlementServiceClient;

    @InjectMocks
    private ClaimService claimService;

    private CustomUserDetails policyholder;
    private Claim savedClaim;

    @BeforeEach
    void setUp() {
        policyholder = new CustomUserDetails("u1", "pholder", "POLICYHOLDER");

        savedClaim = new Claim();
        savedClaim.setClaimNumber("CLM1002");
        savedClaim.setPolicyNumber("POL1");
        savedClaim.setClaimedAmount(new BigDecimal("5000"));
        savedClaim.setStatus(ClaimStatus.SUBMITTED);
        savedClaim.setClaimantUserId("u1");
    }

    @Test
    void submitClaim_ValidCoverage_StatusValidated() {
        ClaimSequence seq = new ClaimSequence();
        seq.setNextVal(1001L);
        when(sequenceRepository.findById("SEQ")).thenReturn(Optional.of(seq));
        when(claimRepository.saveAndFlush(any(Claim.class))).thenReturn(savedClaim);
        when(claimRepository.save(any(Claim.class))).thenReturn(savedClaim);

        CoverageValidationResponse covRes = new CoverageValidationResponse();
        covRes.setValid(true);
        when(policyServiceClient.validateCoverage(eq("POL1"), any(CoverageValidationRequest.class))).thenReturn(covRes);

        SubmitClaimRequest submitReq = new SubmitClaimRequest();
        submitReq.setPolicyNumber("POL1");
        submitReq.setClaimedAmount(new BigDecimal("5000"));
        submitReq.setIncidentDate(LocalDate.now());
        submitReq.setDescription("Car crash");

        ClaimDto dto = claimService.submitClaim(submitReq, policyholder);

        assertEquals(ClaimStatus.VALIDATED, savedClaim.getStatus());
    }

    @Test
    void approveClaim_Success_CallsSettlement() {
        savedClaim.setStatus(ClaimStatus.VALIDATED);
        when(claimRepository.findByClaimNumber("CLM1002")).thenReturn(Optional.of(savedClaim));
        when(claimRepository.saveAndFlush(any(Claim.class))).thenReturn(savedClaim);
        when(claimRepository.save(any(Claim.class))).thenReturn(savedClaim);

        PolicyDto pDto = new PolicyDto();
        pDto.setDeductible(new BigDecimal("500"));
        when(policyServiceClient.getPolicy("POL1")).thenReturn(pDto);

        SettlementDto sDto = new SettlementDto();
        sDto.setStatus("COMPLETED");
        when(settlementServiceClient.processSettlement(any(ProcessSettlementRequest.class))).thenReturn(sDto);

        ApproveClaimRequest appReq = new ApproveClaimRequest();
        appReq.setApprovedAmount(new BigDecimal("4000"));

        ClaimDto dto = claimService.approveClaim("CLM1002", appReq);

        assertEquals(ClaimStatus.SETTLED, dto.getStatus());
        assertEquals(new BigDecimal("4000"), dto.getApprovedAmount());
    }
}
