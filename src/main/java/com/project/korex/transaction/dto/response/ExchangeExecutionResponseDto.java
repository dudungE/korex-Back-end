package com.project.korex.transaction.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ExchangeExecutionResponseDto {

    private Long transactionId;
    private boolean success;
    private BigDecimal total_deducted_amount;
    private BigDecimal appliedRate;
    private BigDecimal fee;
    private String message;
    private LocalDateTime executedAt;
}
