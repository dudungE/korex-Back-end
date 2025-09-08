package com.project.korex.transaction.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ExchangeResultDto {

    private boolean success;
    private Long exchangeId;
    private BigDecimal total_deducted_amount;
    private BigDecimal appliedRate;
    private BigDecimal fee;
}
