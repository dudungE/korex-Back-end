package com.project.korex.transaction.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BalanceResponseDto {

    private String currencyCode;
    private BigDecimal availableAmount;
    private BigDecimal heldAmount;
    private String accountType;
    private String displayAmount;
}
