package com.vanguard.claimservice.dto;

import java.math.BigDecimal;

public class PolicyDto {
    private String policyNumber;
    private BigDecimal deductible;

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    
    public BigDecimal getDeductible() { return deductible; }
    public void setDeductible(BigDecimal deductible) { this.deductible = deductible; }
}
