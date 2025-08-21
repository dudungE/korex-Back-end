package com.project.korex.transaction.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrossRateDetailsDto {
    private BigDecimal step1Rate;        // 1단계 환율 (USD → KRW)
    private String step1Description;     // "USD → KRW"
    private BigDecimal step2Rate;        // 2단계 환율 (KRW → EUR)
    private String step2Description;     // "KRW → EUR"
    private BigDecimal finalRate;        // 최종 환율
}