package com.project.korex.exchangeRate.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CurrencyRateDto {
    private String currencyCode;      // 통화 단위 (ex: USD)
    private LocalDate baseDate;       // 기준 날짜
    private BigDecimal baseRate;      // 기준 환율
    private String changeDirection;   // 상승/하락
    private BigDecimal changeAmount;  // 변화량
    private BigDecimal buyCashRate;   // 현찰 살 때
    private BigDecimal sellCashRate;  // 현찰 팔 때
    private BigDecimal sendRate;      // 송금 보낼 때
    private BigDecimal receiveRate;   // 송금 받을 때
}
