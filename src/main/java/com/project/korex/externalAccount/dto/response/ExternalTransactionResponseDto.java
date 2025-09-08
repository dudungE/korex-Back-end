package com.project.korex.externalAccount.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalTransactionResponseDto {

    private Long transactionId;
    private String type;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal finalAmount;
    private String status;
    private String description;
    private LocalDateTime createdAt;
}
