package com.vanguard.settlementservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SettlementSequence {
    @Id
    private String id = "SEQ";
    private Long nextVal = 1001L;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public Long getNextVal() { return nextVal; }
    public void setNextVal(Long nextVal) { this.nextVal = nextVal; }
}
