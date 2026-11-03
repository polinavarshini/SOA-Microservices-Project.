package com.vanguard.claimservice.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String claimNumber;

    @Column(nullable = false)
    private String policyNumber;

    @Column(nullable = false)
    private String claimantUserId;

    @Column(nullable = false)
    private String claimantUsername;

    @Column(nullable = false)
    private BigDecimal claimedAmount;

    private BigDecimal approvedAmount;

    @Column(nullable = false)
    private LocalDate incidentDate;

    @Column(nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status;

    private String rejectionReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    
    public String getClaimNumber() { return claimNumber; }
    public void setClaimNumber(String claimNumber) { this.claimNumber = claimNumber; }
    
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    
    public String getClaimantUserId() { return claimantUserId; }
    public void setClaimantUserId(String claimantUserId) { this.claimantUserId = claimantUserId; }
    
    public String getClaimantUsername() { return claimantUsername; }
    public void setClaimantUsername(String claimantUsername) { this.claimantUsername = claimantUsername; }
    
    public BigDecimal getClaimedAmount() { return claimedAmount; }
    public void setClaimedAmount(BigDecimal claimedAmount) { this.claimedAmount = claimedAmount; }
    
    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public void setApprovedAmount(BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; }
    
    public LocalDate getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDate incidentDate) { this.incidentDate = incidentDate; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }
    
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
