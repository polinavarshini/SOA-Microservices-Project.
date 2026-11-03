package com.vanguard.settlementservice.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "settlements")
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String settlementNumber;

    @Column(nullable = false, unique = true)
    private String claimNumber;

    @Column(nullable = false)
    private String policyNumber;

    @Column(nullable = false)
    private BigDecimal approvedAmount;

    @Column(nullable = false)
    private BigDecimal deductible;

    @Column(nullable = false)
    private BigDecimal payoutAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementStatus status;

    private LocalDateTime processedAt;

    private String failureReason;

    public UUID getId() { return id; }
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
