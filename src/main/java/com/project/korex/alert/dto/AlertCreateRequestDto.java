package com.project.korex.alert.dto;

import com.project.korex.alert.domain.AlertCondition;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class AlertCreateRequestDto {

    @NotBlank(message = "통화 코드는 필수입니다.")
    private String currencyCode;

    @NotNull(message = "목표 환율은 필수입니다.")
    @DecimalMin(value = "0.0", inclusive = false, message = "목표 환율은 0보다 커야 합니다.")
    private BigDecimal targetRate;

    @NotNull(message = "알람 조건은 필수입니다.")
    private AlertCondition condition;
}
