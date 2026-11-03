package com.vanguard.claimservice.client;

import com.vanguard.claimservice.dto.ProcessSettlementRequest;
import com.vanguard.claimservice.dto.SettlementDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "SETTLEMENT-SERVICE")
public interface SettlementServiceClient {

    @PostMapping("/api/settlements/process")
    SettlementDto processSettlement(@RequestBody ProcessSettlementRequest request);
}
