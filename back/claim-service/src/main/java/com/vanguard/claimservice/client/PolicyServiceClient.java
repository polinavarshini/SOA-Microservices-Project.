package com.vanguard.claimservice.client;

import com.vanguard.claimservice.dto.CoverageValidationRequest;
import com.vanguard.claimservice.dto.CoverageValidationResponse;
import com.vanguard.claimservice.dto.PolicyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "POLICY-SERVICE")
public interface PolicyServiceClient {

    @PostMapping("/api/policies/{policyNumber}/validate-coverage")
    CoverageValidationResponse validateCoverage(
            @PathVariable("policyNumber") String policyNumber,
            @RequestBody CoverageValidationRequest request);

    @GetMapping("/api/policies/{policyNumber}")
    PolicyDto getPolicy(@PathVariable("policyNumber") String policyNumber);
}
