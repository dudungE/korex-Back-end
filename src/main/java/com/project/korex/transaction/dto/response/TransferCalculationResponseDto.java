package com.project.korex.transaction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferCalculationResponseDto {

    private BigDecimal sendAmount;
    private BigDecimal receiveAmount;
    private BigDecimal exchangeRateApplied;
    private BigDecimal feeAmount;
    private BigDecimal totalDeductedAmount;
    private String status;
}
