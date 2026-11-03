package com.vanguard.policyservice.controller;

import com.vanguard.policyservice.dto.PolicyDto;
import com.vanguard.policyservice.entity.PolicyStatus;
import com.vanguard.policyservice.entity.PolicyType;
import com.vanguard.policyservice.security.CustomUserDetails;
import com.vanguard.policyservice.service.PolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PolicyControllerSecurityTest {

    @Mock
    private PolicyService policyService;

    @InjectMocks
    private PolicyController policyController;

    private PolicyDto mockPolicy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockPolicy = new PolicyDto();
        mockPolicy.setPolicyNumber("POL1");
        mockPolicy.setHolderUserId("user-1");
    }

    @Test
    void getPolicy_AdjusterCanAccessAny() {
        CustomUserDetails adjuster = new CustomUserDetails("adj-1", "adjuster", "ADJUSTER");
        when(policyService.getPolicyByNumber("POL1")).thenReturn(mockPolicy);

        ResponseEntity<PolicyDto> response = policyController.getPolicy("POL1", adjuster);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("POL1", response.getBody().getPolicyNumber());
    }

    @Test
    void getPolicy_PolicyholderCanAccessOwn() {
        CustomUserDetails owner = new CustomUserDetails("user-1", "jdoe", "POLICYHOLDER");
        when(policyService.getPolicyByNumber("POL1")).thenReturn(mockPolicy);

        ResponseEntity<PolicyDto> response = policyController.getPolicy("POL1", owner);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getPolicy_PolicyholderCannotAccessOthers() {
        CustomUserDetails other = new CustomUserDetails("user-2", "other", "POLICYHOLDER");
        when(policyService.getPolicyByNumber("POL1")).thenReturn(mockPolicy);

        ResponseEntity<PolicyDto> response = policyController.getPolicy("POL1", other);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void getPolicies_AdjusterSeesAll() {
        CustomUserDetails adjuster = new CustomUserDetails("adj-1", "adjuster", "ADJUSTER");
        when(policyService.getAllPolicies()).thenReturn(Collections.singletonList(mockPolicy));

        ResponseEntity<List<PolicyDto>> response = policyController.getPolicies(adjuster);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(policyService, times(1)).getAllPolicies();
        verify(policyService, never()).getPoliciesByHolder(anyString());
    }

    @Test
    void getPolicies_PolicyholderSeesOnlyOwn() {
        CustomUserDetails owner = new CustomUserDetails("user-1", "jdoe", "POLICYHOLDER");
        when(policyService.getPoliciesByHolder("user-1")).thenReturn(Collections.singletonList(mockPolicy));

        ResponseEntity<List<PolicyDto>> response = policyController.getPolicies(owner);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(policyService, never()).getAllPolicies();
        verify(policyService, times(1)).getPoliciesByHolder("user-1");
    }
}
