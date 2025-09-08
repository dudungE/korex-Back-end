package com.project.korex.transaction.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ExchangeSimulationResponseDto {

    private boolean success;              // true
    private BigDecimal fromAmount;        // 100000
    private BigDecimal toAmount;          // 71.50
    private BigDecimal exchangeRate;      // 1398.60
    private BigDecimal fee;               // 500
    private BigDecimal totalDeductedAmount;       // 70.85
    private String rateUpdateTime;        // "14:30:25"
    private String message;               // 에러 시에만 사용
}

