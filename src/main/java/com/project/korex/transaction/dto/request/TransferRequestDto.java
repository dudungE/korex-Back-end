package com.project.korex.transaction.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDto {

    @NotBlank(message = "수취인 전화번호를 입력해주세요")
    @Pattern(regexp = "^01[0-9]-?[0-9]{4}-?[0-9]{4}$", message = "올바른 휴대폰 번호 형식이 아닙니다")
    private String recipientPhone;

    @NotBlank(message = "수취인 이름을 입력해주세요")
    @Size(min = 2, max = 10, message = "수취인 이름은 2~10자여야 합니다")
    private String recipientName;

    @NotBlank(message = "보낼 통화를 선택해주세요")
    private String fromCurrencyCode;

    @NotBlank(message = "받을 통화를 선택해주세요")
    private String toCurrencyCode;

    @NotNull(message = "송금 금액을 입력해주세요")
    @DecimalMin(value = "1", message = "송금 금액은 1보다 커야 합니다")
    private BigDecimal sendAmount;

    // 4자리 비밀번호로 수정
    @NotBlank(message = "거래 비밀번호를 입력해주세요")
    @Pattern(regexp = "^[0-9]{4}$", message = "거래 비밀번호는 4자리 숫자여야 합니다")
    private String transactionPassword;
}
