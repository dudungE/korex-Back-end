package com.project.korex.ForeignTransfer.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ForeignTransferResponse {

    private Long transferId;
    private Long senderId;
    private String accountType;
    private BigDecimal transferAmount;
    private BigDecimal convertedAmount;
    private BigDecimal appliedRate;
    private BigDecimal feeAmount;       // 추가
    private BigDecimal feePercentage;   // 추가
    private String withdrawalMethod;
    private String transferReason;
    private String relationRecipient;
    private String requestStatus;
    private String transferStatus;
    private LocalDateTime createdAt;

    private boolean termsAgreed;
    private LocalDateTime agreedAt;
}
