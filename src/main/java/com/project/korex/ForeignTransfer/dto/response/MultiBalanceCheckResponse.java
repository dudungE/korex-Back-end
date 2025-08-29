package com.project.korex.ForeignTransfer.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MultiBalanceCheckResponse {
    private String accountType;
    private String currencyCode;
    private BigDecimal availableAmount;
}