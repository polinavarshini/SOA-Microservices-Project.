package com.vanguard.settlementservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class ProcessSettlementRequest {
    @NotBlank
    private String claimNumber;
    @NotBlank
    private String policyNumber;
    @NotNull @PositiveOrZero
    private BigDecimal approvedAmount;
    @NotNull @PositiveOrZero
    private BigDecimal deductible;

    public String getClaimNumber() { return claimNumber; }
    public void setClaimNumber(String claimNumber) { this.claimNumber = claimNumber; }
    
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    
    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public void setApprovedAmount(BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; }
    
    public BigDecimal getDeductible() { return deductible; }
    public void setDeductible(BigDecimal deductible) { this.deductible = deductible; }
}
