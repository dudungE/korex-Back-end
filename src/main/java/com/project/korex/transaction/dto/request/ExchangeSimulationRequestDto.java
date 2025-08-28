package com.project.korex.transaction.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Valid
public class ExchangeSimulationRequestDto {
    @NotBlank(message = "출금 통화는 필수입니다")
    private String fromCurrency;    // 예: "KRW"

    @NotBlank(message = "입금 통화는 필수입니다")
    private String toCurrency;      // 예: "USD"

    @NotNull(message = "환전 금액은 필수입니다")
    private BigDecimal amount;      // 예: 100000
}
