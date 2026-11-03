package com.vanguard.claimservice.dto;

import java.math.BigDecimal;

public class CoverageValidationResponse {
    private boolean valid;
    private String reason;
    private BigDecimal coverageLimit;
    private BigDecimal remainingCoverage;
    private BigDecimal deductible;

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public BigDecimal getCoverageLimit() { return coverageLimit; }
    public void setCoverageLimit(BigDecimal coverageLimit) { this.coverageLimit = coverageLimit; }
    public BigDecimal getRemainingCoverage() { return remainingCoverage; }
    public void setRemainingCoverage(BigDecimal remainingCoverage) { this.remainingCoverage = remainingCoverage; }
    public BigDecimal getDeductible() { return deductible; }
    public void setDeductible(BigDecimal deductible) { this.deductible = deductible; }
}
