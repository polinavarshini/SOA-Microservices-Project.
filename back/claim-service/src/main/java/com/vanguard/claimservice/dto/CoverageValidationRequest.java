package com.vanguard.claimservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CoverageValidationRequest {
    private BigDecimal claimedAmount;
    private LocalDate incidentDate;
    
    public CoverageValidationRequest() {}
    public CoverageValidationRequest(BigDecimal claimedAmount, LocalDate incidentDate) {
        this.claimedAmount = claimedAmount;
        this.incidentDate = incidentDate;
    }
    public BigDecimal getClaimedAmount() { return claimedAmount; }
    public void setClaimedAmount(BigDecimal claimedAmount) { this.claimedAmount = claimedAmount; }
    public LocalDate getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDate incidentDate) { this.incidentDate = incidentDate; }
}
