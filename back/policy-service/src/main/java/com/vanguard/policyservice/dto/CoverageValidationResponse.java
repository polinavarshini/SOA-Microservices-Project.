package com.vanguard.policyservice.dto;

import java.math.BigDecimal;

public class CoverageValidationResponse {
    private boolean valid;
    private String reason;
    private BigDecimal coverageLimit;
    private BigDecimal remainingCoverage;
    private BigDecimal deductible;

    public CoverageValidationResponse(boolean valid, String reason, BigDecimal coverageLimit, BigDecimal remainingCoverage, BigDecimal deductible) {
        this.valid = valid;
        this.reason = reason;
        this.coverageLimit = coverageLimit;
        this.remainingCoverage = remainingCoverage;
        this.deductible = deductible;
    }

    public boolean isValid() { return valid; }
    public String getReason() { return reason; }
    public BigDecimal getCoverageLimit() { return coverageLimit; }
    public BigDecimal getRemainingCoverage() { return remainingCoverage; }
    public BigDecimal getDeductible() { return deductible; }
}
