package com.project.korex.ForeignTransfer.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class TransferExchangeResponse {
    private final BigDecimal fromAmount;
    private final BigDecimal toAmount;
    private final BigDecimal exchangeRate;
    private final BigDecimal fee;
    private final BigDecimal totalDeductedAmount;
    private final LocalDateTime rateUpdateTime;
}
