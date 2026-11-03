package com.vanguard.claimservice.controller;

import com.vanguard.claimservice.dto.ApproveClaimRequest;
import com.vanguard.claimservice.dto.ClaimDto;
import com.vanguard.claimservice.dto.RejectClaimRequest;
import com.vanguard.claimservice.dto.SubmitClaimRequest;
import com.vanguard.claimservice.security.CustomUserDetails;
import com.vanguard.claimservice.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    @PreAuthorize("hasRole('POLICYHOLDER')")
    public ResponseEntity<ClaimDto> submitClaim(
            @Valid @RequestBody SubmitClaimRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        return new ResponseEntity<>(claimService.submitClaim(request, user), HttpStatus.CREATED);
    }

    @GetMapping("/{claimNumber}")
    public ResponseEntity<ClaimDto> getClaim(
            @PathVariable String claimNumber,
            @AuthenticationPrincipal CustomUserDetails user) {
        try {
            return ResponseEntity.ok(claimService.getClaim(claimNumber, user));
        } catch (RuntimeException e) {
            if ("Access Denied".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ClaimDto>> getClaims(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(claimService.getClaims(user));
    }

    @PostMapping("/{claimNumber}/validate")
    @PreAuthorize("hasRole('ADJUSTER')")
    public ResponseEntity<ClaimDto> validateClaim(@PathVariable String claimNumber) {
        return ResponseEntity.ok(claimService.validateClaim(claimNumber));
    }

    @PutMapping("/{claimNumber}/approve")
    @PreAuthorize("hasRole('ADJUSTER')")
    public ResponseEntity<ClaimDto> approveClaim(
            @PathVariable String claimNumber,
            @Valid @RequestBody ApproveClaimRequest request) {
        return ResponseEntity.ok(claimService.approveClaim(claimNumber, request));
    }

    @PutMapping("/{claimNumber}/reject")
    @PreAuthorize("hasRole('ADJUSTER')")
    public ResponseEntity<ClaimDto> rejectClaim(
            @PathVariable String claimNumber,
            @Valid @RequestBody RejectClaimRequest request) {
        return ResponseEntity.ok(claimService.rejectClaim(claimNumber, request));
    }
}
