package com.vanguard.policyservice.dto;

import com.vanguard.policyservice.entity.PolicyStatus;
import com.vanguard.policyservice.entity.PolicyType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class PolicyDto {
    private UUID id;
    private String policyNumber;
    private String holderUserId;
    private String holderUsername;
    private PolicyType policyType;
    private BigDecimal coverageLimit;
    private BigDecimal deductible;
    private BigDecimal premium;
    private LocalDate startDate;
    private LocalDate endDate;
    private PolicyStatus status;
    private BigDecimal totalClaimed;

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    
    public String getHolderUserId() { return holderUserId; }
    public void setHolderUserId(String holderUserId) { this.holderUserId = holderUserId; }
    
    public String getHolderUsername() { return holderUsername; }
    public void setHolderUsername(String holderUsername) { this.holderUsername = holderUsername; }
    
    public PolicyType getPolicyType() { return policyType; }
    public void setPolicyType(PolicyType policyType) { this.policyType = policyType; }
    
    public BigDecimal getCoverageLimit() { return coverageLimit; }
    public void setCoverageLimit(BigDecimal coverageLimit) { this.coverageLimit = coverageLimit; }
    
    public BigDecimal getDeductible() { return deductible; }
    public void setDeductible(BigDecimal deductible) { this.deductible = deductible; }
    
    public BigDecimal getPremium() { return premium; }
    public void setPremium(BigDecimal premium) { this.premium = premium; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public PolicyStatus getStatus() { return status; }
    public void setStatus(PolicyStatus status) { this.status = status; }
    
    public BigDecimal getTotalClaimed() { return totalClaimed; }
    public void setTotalClaimed(BigDecimal totalClaimed) { this.totalClaimed = totalClaimed; }
}
