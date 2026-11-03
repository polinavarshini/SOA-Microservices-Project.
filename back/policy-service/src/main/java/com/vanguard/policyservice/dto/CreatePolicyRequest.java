package com.vanguard.policyservice.dto;

import com.vanguard.policyservice.entity.PolicyStatus;
import com.vanguard.policyservice.entity.PolicyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CreatePolicyRequest {
    @NotBlank
    private String holderUserId;
    @NotBlank
    private String holderUsername;
    @NotNull
    private PolicyType policyType;
    @NotNull @PositiveOrZero
    private BigDecimal coverageLimit;
    @NotNull @PositiveOrZero
    private BigDecimal deductible;
    @NotNull @PositiveOrZero
    private BigDecimal premium;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @NotNull
    private PolicyStatus status;

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
}
