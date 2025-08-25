package com.project.korex.transaction.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferCalculationRequestDto {

    @NotBlank(message = "보낼 통화를 입력해주세요. ")
    private String fromCurrencyCode;

    @NotBlank(message = "받을 통화를 입력해주세요. ")
    private String toCurrencyCode;

    @NotBlank(message = "송금 금액을 입력해주세요. ")
    @DecimalMin(value = "1", message = "최소 금액은 1 이상입니다.")
    private BigDecimal sendAmount;
}
