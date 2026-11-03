package com.vanguard.claimservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SubmitClaimRequest {
    @NotBlank
    private String policyNumber;
    @NotNull @Positive
    private BigDecimal claimedAmount;
    @NotNull
    private LocalDate incidentDate;
    @NotBlank
    private String description;

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    
    public BigDecimal getClaimedAmount() { return claimedAmount; }
    public void setClaimedAmount(BigDecimal claimedAmount) { this.claimedAmount = claimedAmount; }
    
    public LocalDate getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDate incidentDate) { this.incidentDate = incidentDate; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
