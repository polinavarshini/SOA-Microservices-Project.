package com.vanguard.settlementservice.service;

import com.vanguard.settlementservice.dto.ProcessSettlementRequest;
import com.vanguard.settlementservice.dto.SettlementDto;
import com.vanguard.settlementservice.entity.Settlement;
import com.vanguard.settlementservice.entity.SettlementSequence;
import com.vanguard.settlementservice.entity.SettlementStatus;
import com.vanguard.settlementservice.repository.SettlementRepository;
import com.vanguard.settlementservice.repository.SettlementSequenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final SettlementSequenceRepository sequenceRepository;

    public SettlementService(SettlementRepository settlementRepository, SettlementSequenceRepository sequenceRepository) {
        this.settlementRepository = settlementRepository;
        this.sequenceRepository = sequenceRepository;
    }

    @Transactional
    public String generateSettlementNumber() {
        SettlementSequence seq = sequenceRepository.findById("SEQ").orElse(new SettlementSequence());
        seq.setNextVal(seq.getNextVal() + 1);
        sequenceRepository.save(seq);
        return "STL" + seq.getNextVal();
    }

    @Transactional
    public SettlementDto processSettlement(ProcessSettlementRequest request) {
        if (settlementRepository.existsByClaimNumber(request.getClaimNumber())) {
            throw new RuntimeException("Settlement already processed for claim: " + request.getClaimNumber());
        }

        Settlement settlement = new Settlement();
        settlement.setSettlementNumber(generateSettlementNumber());
        settlement.setClaimNumber(request.getClaimNumber());
        settlement.setPolicyNumber(request.getPolicyNumber());
        settlement.setApprovedAmount(request.getApprovedAmount());
        settlement.setDeductible(request.getDeductible());

        BigDecimal payout = request.getApprovedAmount().subtract(request.getDeductible());
        if (payout.compareTo(BigDecimal.ZERO) < 0) {
            payout = BigDecimal.ZERO;
        }
        settlement.setPayoutAmount(payout);
        settlement.setStatus(SettlementStatus.COMPLETED);
        settlement.setProcessedAt(LocalDateTime.now());

        return mapToDto(settlementRepository.save(settlement));
    }

    public SettlementDto getSettlementByNumber(String settlementNumber) {
        return settlementRepository.findBySettlementNumber(settlementNumber)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Settlement not found"));
    }

    public SettlementDto getSettlementByClaimNumber(String claimNumber) {
        return settlementRepository.findByClaimNumber(claimNumber)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Settlement not found for claim"));
    }

    private SettlementDto mapToDto(Settlement settlement) {
        SettlementDto dto = new SettlementDto();
        dto.setSettlementNumber(settlement.getSettlementNumber());
        dto.setClaimNumber(settlement.getClaimNumber());
        dto.setPolicyNumber(settlement.getPolicyNumber());
        dto.setApprovedAmount(settlement.getApprovedAmount());
        dto.setDeductible(settlement.getDeductible());
        dto.setPayoutAmount(settlement.getPayoutAmount());
        dto.setStatus(settlement.getStatus());
        dto.setProcessedAt(settlement.getProcessedAt());
        dto.setFailureReason(settlement.getFailureReason());
        return dto;
    }
}
