package com.project.korex.externalAccount.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawRequestDto {
    @NotNull(message = "출금 금액을 입력하세요")
    @DecimalMin(value = "1000", message = "최소 출금 금액은 1,000원입니다")
    private BigDecimal amount;

    @NotBlank(message = "거래 비밀번호를 입력하세요")
    @Size(min = 4, max = 4, message = "거래 비밀번호는 4자리입니다")
    private String transactionPassword;
}
