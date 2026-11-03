package com.vanguard.policyservice.service;

import com.vanguard.policyservice.dto.CoverageValidationRequest;
import com.vanguard.policyservice.dto.CoverageValidationResponse;
import com.vanguard.policyservice.entity.Policy;
import com.vanguard.policyservice.entity.PolicyStatus;
import com.vanguard.policyservice.entity.PolicyType;
import com.vanguard.policyservice.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyService policyService;

    private Policy validPolicy;

    @BeforeEach
    void setUp() {
        validPolicy = new Policy();
        validPolicy.setId(UUID.randomUUID());
        validPolicy.setPolicyNumber("POL12345");
        validPolicy.setHolderUserId("user-1");
        validPolicy.setHolderUsername("jdoe");
        validPolicy.setPolicyType(PolicyType.PROPERTY);
        validPolicy.setCoverageLimit(new BigDecimal("100000"));
        validPolicy.setDeductible(new BigDecimal("500"));
        validPolicy.setPremium(new BigDecimal("1000"));
        validPolicy.setStartDate(LocalDate.now().minusDays(10));
        validPolicy.setEndDate(LocalDate.now().plusYears(1));
        validPolicy.setStatus(PolicyStatus.ACTIVE);
        validPolicy.setTotalClaimed(new BigDecimal("10000"));
    }

    @Test
    void testValidateCoverage_Success() {
        when(policyRepository.findByPolicyNumber("POL12345")).thenReturn(Optional.of(validPolicy));

        CoverageValidationRequest req = new CoverageValidationRequest();
        req.setClaimedAmount(new BigDecimal("50000"));
        req.setIncidentDate(LocalDate.now());

        CoverageValidationResponse res = policyService.validateCoverage("POL12345", req);

        assertTrue(res.isValid());
        assertEquals("Valid", res.getReason());
        assertEquals(new BigDecimal("90000"), res.getRemainingCoverage());
    }

    @Test
    void testValidateCoverage_NotFound() {
        when(policyRepository.findByPolicyNumber("POL999")).thenReturn(Optional.empty());

        CoverageValidationRequest req = new CoverageValidationRequest();
        req.setClaimedAmount(new BigDecimal("50000"));
        req.setIncidentDate(LocalDate.now());

        CoverageValidationResponse res = policyService.validateCoverage("POL999", req);

        assertFalse(res.isValid());
        assertEquals("Policy not found", res.getReason());
    }

    @Test
    void testValidateCoverage_NotActive() {
        validPolicy.setStatus(PolicyStatus.EXPIRED);
        when(policyRepository.findByPolicyNumber("POL12345")).thenReturn(Optional.of(validPolicy));

        CoverageValidationRequest req = new CoverageValidationRequest();
        req.setClaimedAmount(new BigDecimal("1000"));
        req.setIncidentDate(LocalDate.now());

        CoverageValidationResponse res = policyService.validateCoverage("POL12345", req);

        assertFalse(res.isValid());
        assertTrue(res.getReason().contains("not ACTIVE"));
    }

    @Test
    void testValidateCoverage_OutsideDates() {
        when(policyRepository.findByPolicyNumber("POL12345")).thenReturn(Optional.of(validPolicy));

        CoverageValidationRequest req = new CoverageValidationRequest();
        req.setClaimedAmount(new BigDecimal("1000"));
        req.setIncidentDate(LocalDate.now().plusYears(2));

        CoverageValidationResponse res = policyService.validateCoverage("POL12345", req);

        assertFalse(res.isValid());
        assertEquals("Incident date is outside policy active dates", res.getReason());
    }

    @Test
    void testValidateCoverage_NegativeClaim() {
        when(policyRepository.findByPolicyNumber("POL12345")).thenReturn(Optional.of(validPolicy));

        CoverageValidationRequest req = new CoverageValidationRequest();
        req.setClaimedAmount(new BigDecimal("-500"));
        req.setIncidentDate(LocalDate.now());

        CoverageValidationResponse res = policyService.validateCoverage("POL12345", req);

        assertFalse(res.isValid());
        assertEquals("Claimed amount must be positive", res.getReason());
    }

    @Test
    void testValidateCoverage_ExceedsRemaining() {
        when(policyRepository.findByPolicyNumber("POL12345")).thenReturn(Optional.of(validPolicy));

        CoverageValidationRequest req = new CoverageValidationRequest();
        req.setClaimedAmount(new BigDecimal("95000")); // Remaining is 90000
        req.setIncidentDate(LocalDate.now());

        CoverageValidationResponse res = policyService.validateCoverage("POL12345", req);

        assertFalse(res.isValid());
        assertEquals("Claimed amount exceeds remaining coverage", res.getReason());
        assertEquals(new BigDecimal("90000"), res.getRemainingCoverage());
    }
}
