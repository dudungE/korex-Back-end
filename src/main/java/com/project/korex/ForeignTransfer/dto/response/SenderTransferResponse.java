package com.project.korex.ForeignTransfer.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SenderTransferResponse {

    private Long transferId;
    private Long senderId;
    private String accountType;
    private BigDecimal availableBalance; // 예상 잔액
    private BigDecimal transferAmount;
    private String withdrawalMethod;
    private String transferReason;
    private String relationRecipient;
    private String requestStatus;
    private String transferStatus;
    private LocalDateTime createdAt;
}
