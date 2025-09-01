package com.project.korex.transaction.dto.response;

import com.project.korex.transaction.enums.TransactionType;
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
public class TransactionResponseDto {
    private Long id;
    private Long fromUserId;
    private String fromUserName;
    private Long toUserId;
    private String toUserName;
    private String fromCurrencyCode;
    private String fromCurrencyName;
    private String toCurrencyCode;
    private String toCurrencyName;
    private BigDecimal sendAmount;
    private BigDecimal receiveAmount;
    private BigDecimal feeAmount;
    private BigDecimal totalDeductedAmount;
    private TransactionType transactionType;
    private String status;
    private LocalDateTime createdAt;
}

