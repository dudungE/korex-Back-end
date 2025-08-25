package com.project.korex.transaction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeCalculationDto {
    private BigDecimal exchangeRate;
    private BigDecimal beforeFeeAmount;
    private BigDecimal convertedAmount;
    private BigDecimal fee;
    private BigDecimal totalDeductedAmount;
}
