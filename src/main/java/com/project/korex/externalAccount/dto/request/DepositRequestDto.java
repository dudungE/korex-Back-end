package com.project.korex.externalAccount.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositRequestDto {
    @NotNull(message = "충전 금액을 입력하세요")
    @DecimalMin(value = "1000", message = "최소 충전 금액은 1,000원입니다")
    @DecimalMax(value = "5000000", message = "최대 충전 금액은 5,000,000원입니다")
    private BigDecimal amount;

    @NotBlank(message = "거래 비밀번호를 입력하세요")
    @Size(min = 4, max = 4, message = "거래 비밀번호는 4자리입니다")
    private String transactionPassword;
}
