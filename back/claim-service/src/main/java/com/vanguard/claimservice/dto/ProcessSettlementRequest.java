package com.vanguard.claimservice.dto;

import java.math.BigDecimal;

public class ProcessSettlementRequest {
    private String claimNumber;
    private String policyNumber;
    private BigDecimal approvedAmount;
    private BigDecimal deductible;

    public ProcessSettlementRequest() {}
    public ProcessSettlementRequest(String claimNumber, String policyNumber, BigDecimal approvedAmount, BigDecimal deductible) {
        this.claimNumber = claimNumber;
        this.policyNumber = policyNumber;
        this.approvedAmount = approvedAmount;
        this.deductible = deductible;
    }

    public String getClaimNumber() { return claimNumber; }
    public void setClaimNumber(String claimNumber) { this.claimNumber = claimNumber; }
    
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    
    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public void setApprovedAmount(BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; }
    
    public BigDecimal getDeductible() { return deductible; }
    public void setDeductible(BigDecimal deductible) { this.deductible = deductible; }
}
