package com.project.korex.externalAccount.dto.request;


import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddExternalAccountRequestDto {

    @NotBlank(message = "은행을 선택하세요")
    private String bankCode;

    @NotBlank(message = "계좌번호를 입력하세요")
    @Pattern(regexp = "^[\\d-]+$", message = "올바른 계좌번호 형식이 아닙니다")
    private String accountNumber;

    @NotBlank(message = "예금주명을 입력하세요")
    @Size(max = 10, message = "예금주명은 10자 이하로 입력하세요")
    private String accountHolder;

    @NotNull(message = "초기 잔액을 입력하세요")
    @DecimalMin(value = "0", message = "잔액은 0원 이상이어야 합니다")
    @DecimalMax(value = "100000000", message = "잔액은 1억원 이하로 입력하세요")
    private BigDecimal balance;

    private Boolean isPrimary = false;
}
