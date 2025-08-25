package com.project.korex.transaction.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * Service에서 계산한 결과를 담는 객체
 *
 */
@Data
@Builder
public class ExchangeSimulationDto{
    private BigDecimal fromAmount;        // 100000 (입력된 금액)
    private BigDecimal toAmount;          // 71.50 (환전될 금액)
    private BigDecimal exchangeRate;      // 1398.60 (적용 환율)
    private BigDecimal fee;               // 500 (수수료)
    private BigDecimal totalDeductedAmount; // 70.85 (최종 받을 금액)
    private String rateUpdateTime;        // "14:30:25"
}
