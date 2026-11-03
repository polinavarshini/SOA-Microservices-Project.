package com.vanguard.claimservice.dto;

import jakarta.validation.constraints.NotBlank;

public class RejectClaimRequest {
    @NotBlank
    private String reason;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
