package com.vanguard.settlementservice.service;

import com.vanguard.settlementservice.dto.ProcessSettlementRequest;
import com.vanguard.settlementservice.dto.SettlementDto;
import com.vanguard.settlementservice.entity.Settlement;
import com.vanguard.settlementservice.entity.SettlementSequence;
import com.vanguard.settlementservice.repository.SettlementRepository;
import com.vanguard.settlementservice.repository.SettlementSequenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SettlementServiceTest {

    @Mock
    private SettlementRepository settlementRepository;

    @Mock
    private SettlementSequenceRepository sequenceRepository;

    @InjectMocks
    private SettlementService settlementService;

    @BeforeEach
    void setUp() {}

    @Test
    void processSettlement_PayoutZeroFloor() {
        SettlementSequence seq = new SettlementSequence();
        seq.setNextVal(1001L);
        when(sequenceRepository.findById("SEQ")).thenReturn(Optional.of(seq));
        when(settlementRepository.existsByClaimNumber("CLM1")).thenReturn(false);
        when(settlementRepository.save(any(Settlement.class))).thenAnswer(i -> i.getArguments()[0]);

        ProcessSettlementRequest req = new ProcessSettlementRequest();
        req.setClaimNumber("CLM1");
        req.setPolicyNumber("POL1");
        req.setApprovedAmount(new BigDecimal("500"));
        req.setDeductible(new BigDecimal("1000"));

        SettlementDto result = settlementService.processSettlement(req);

        assertEquals(new BigDecimal("0"), result.getPayoutAmount());
        assertEquals("COMPLETED", result.getStatus().name());
    }

    @Test
    void processSettlement_NormalPayout() {
        SettlementSequence seq = new SettlementSequence();
        seq.setNextVal(1001L);
        when(sequenceRepository.findById("SEQ")).thenReturn(Optional.of(seq));
        when(settlementRepository.existsByClaimNumber("CLM1")).thenReturn(false);
        when(settlementRepository.save(any(Settlement.class))).thenAnswer(i -> i.getArguments()[0]);

        ProcessSettlementRequest req = new ProcessSettlementRequest();
        req.setClaimNumber("CLM1");
        req.setPolicyNumber("POL1");
        req.setApprovedAmount(new BigDecimal("5000"));
        req.setDeductible(new BigDecimal("1000"));

        SettlementDto result = settlementService.processSettlement(req);

        assertEquals(new BigDecimal("4000"), result.getPayoutAmount());
        assertEquals("COMPLETED", result.getStatus().name());
    }

    @Test
    void processSettlement_DuplicateThrowsException() {
        when(settlementRepository.existsByClaimNumber("CLM1")).thenReturn(true);

        ProcessSettlementRequest req = new ProcessSettlementRequest();
        req.setClaimNumber("CLM1");

        assertThrows(RuntimeException.class, () -> settlementService.processSettlement(req));
    }
}
