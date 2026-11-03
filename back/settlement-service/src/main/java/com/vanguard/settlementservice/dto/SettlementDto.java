package com.vanguard.settlementservice.dto;

import com.vanguard.settlementservice.entity.SettlementStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SettlementDto {
    private String settlementNumber;
    private String claimNumber;
    private String policyNumber;
    private BigDecimal approvedAmount;
    private BigDecimal deductible;
    private BigDecimal payoutAmount;
    private SettlementStatus status;
    private LocalDateTime processedAt;
    private String failureReason;

    public String getSettlementNumber() { return settlementNumber; }
    public void setSettlementNumber(String settlementNumber) { this.settlementNumber = settlementNumber; }
    
    public String getClaimNumber() { return claimNumber; }
    public void setClaimNumber(String claimNumber) { this.claimNumber = claimNumber; }
    
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    
    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public void setApprovedAmount(BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; }
    
    public BigDecimal getDeductible() { return deductible; }
    public void setDeductible(BigDecimal deductible) { this.deductible = deductible; }
    
    public BigDecimal getPayoutAmount() { return payoutAmount; }
    public void setPayoutAmount(BigDecimal payoutAmount) { this.payoutAmount = payoutAmount; }
    
    public SettlementStatus getStatus() { return status; }
    public void setStatus(SettlementStatus status) { this.status = status; }
    
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
}
