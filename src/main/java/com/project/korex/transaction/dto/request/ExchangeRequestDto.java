package com.project.korex.transaction.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Valid
public class ExchangeRequestDto {
    @NotBlank(message = "출금 통화는 필수입니다")
    @Pattern(regexp = "^(KRW|USD|JPY|EUR|CNY)$", message = "지원되지 않는 통화입니다")
    private String fromCurrency;

    @NotBlank(message = "입금 통화는 필수입니다")
    @Pattern(regexp = "^(KRW|USD|JPY|EUR|CNY)$", message = "지원되지 않는 통화입니다")
    private String toCurrency;

    @NotNull(message = "환전 금액은 필수입니다")
    @DecimalMin(value = "0.01", message = "환전 금액은 0.01 이상이어야 합니다")
    private BigDecimal amount;

    @Size(min = 4, max = 4, message = "거래 비밀번호는 4자리여야 합니다")
    private String transactionPassword;
}
