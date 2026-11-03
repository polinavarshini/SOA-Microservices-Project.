package com.vanguard.policyservice.controller;

import com.vanguard.policyservice.dto.CoverageValidationRequest;
import com.vanguard.policyservice.dto.CoverageValidationResponse;
import com.vanguard.policyservice.dto.CreatePolicyRequest;
import com.vanguard.policyservice.dto.PolicyDto;
import com.vanguard.policyservice.security.CustomUserDetails;
import com.vanguard.policyservice.service.PolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADJUSTER')")
    public ResponseEntity<PolicyDto> createPolicy(@Valid @RequestBody CreatePolicyRequest request) {
        return new ResponseEntity<>(policyService.createPolicy(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PolicyDto>> getPolicies(@AuthenticationPrincipal CustomUserDetails user) {
        if ("ADJUSTER".equals(user.getRole())) {
            return ResponseEntity.ok(policyService.getAllPolicies());
        }
        return ResponseEntity.ok(policyService.getPoliciesByHolder(user.getUserId()));
    }

    @GetMapping("/{policyNumber}")
    public ResponseEntity<PolicyDto> getPolicy(@PathVariable String policyNumber, @AuthenticationPrincipal CustomUserDetails user) {
        PolicyDto policy = policyService.getPolicyByNumber(policyNumber);
        if ("ADJUSTER".equals(user.getRole()) || policy.getHolderUserId().equals(user.getUserId())) {
            return ResponseEntity.ok(policy);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("/{policyNumber}/validate-coverage")
    public ResponseEntity<CoverageValidationResponse> validateCoverage(
            @PathVariable String policyNumber,
            @Valid @RequestBody CoverageValidationRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        
        // Ensure only adjuster can validate or a policyholder validating their own
        PolicyDto policy = policyService.getPolicyByNumber(policyNumber);
        if (!"ADJUSTER".equals(user.getRole()) && !policy.getHolderUserId().equals(user.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(policyService.validateCoverage(policyNumber, request));
    }
}
