package com.project.korex.ForeignTransfer.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SenderTransferResponse {
    private Long transferId;
    private Long senderId;
    private String accountType;
    private BigDecimal availableBalance;
    private BigDecimal transferAmount;
    private String withdrawalMethod;  // 요청 그대로 반환
    private String transferReason;
    private String relationRecipient;
    private String requestStatus;
    private String transferStatus;
    private LocalDateTime createdAt;
    private BigDecimal convertedAmount;
    private BigDecimal appliedRate;
}

