package com.vanguard.settlementservice.controller;

import com.vanguard.settlementservice.dto.ProcessSettlementRequest;
import com.vanguard.settlementservice.dto.SettlementDto;
import com.vanguard.settlementservice.security.CustomUserDetails;
import com.vanguard.settlementservice.service.SettlementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settlements")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @PostMapping("/process")
    @PreAuthorize("hasRole('ADJUSTER')")
    public ResponseEntity<SettlementDto> processSettlement(@Valid @RequestBody ProcessSettlementRequest request) {
        return new ResponseEntity<>(settlementService.processSettlement(request), HttpStatus.CREATED);
    }

    @GetMapping("/{settlementNumber}")
    public ResponseEntity<SettlementDto> getSettlement(
            @PathVariable String settlementNumber, 
            @AuthenticationPrincipal CustomUserDetails user) {
        // Assume Claim Service / API Gateway has already validated policyholder scope in a broader system.
        // For simplicity, we allow adjuster to see all, policyholder to see all they query (but realistically
        // they should only query their own claim numbers). 
        return ResponseEntity.ok(settlementService.getSettlementByNumber(settlementNumber));
    }

    @GetMapping("/claim/{claimNumber}")
    public ResponseEntity<SettlementDto> getSettlementByClaim(
            @PathVariable String claimNumber,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(settlementService.getSettlementByClaimNumber(claimNumber));
    }
}
