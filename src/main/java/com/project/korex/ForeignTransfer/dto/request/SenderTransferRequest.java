package com.project.korex.ForeignTransfer.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SenderTransferRequest {

    private Long userId;                 // 송금인
    private String accountType;          // "KRW" / "FOREIGN" 등
    private BigDecimal transferAmount;   // 출금 금액
    private String withdrawalMethod;     // 출금 방식
    private String transferReason;       // 송금 사유
    private String relationRecipient;    // 수취인과 관계
    private String accountPassword;      // 계좌 비밀번호
}
