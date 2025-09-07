package com.project.korex.ForeignTransfer.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ForeignTransferHistoryResponse {
    private Long transferId;
    private BigDecimal transferAmount;        // 원화 송금 금액
    private BigDecimal convertedAmount;       // 환전 후 금액
    private BigDecimal appliedRate;           // 적용 환율
    private BigDecimal feeAmount;             // 수수료
    private BigDecimal totalDeductedAmount;   // 총 차감 금액
    private String accountType;               // KRW / FOREIGN
    private String transferStatus;            // NOT_STARTED / IN_PROGRESS / COMPLETED
    private String requestStatus;             // SUBMITTED / APPROVED 등
    private String transferReason;            // 송금 사유
    private String relationRecipient;         // 수취인과 관계
    private String senderName;                // 송금인 이름
    private String countryNumber;
    private String phoneNumber;
    private String email;
    private String country;
    private String engAddress;
    private String accountNumber;             // 송금 계좌 번호
    private LocalDateTime createdAt;
    private LocalDateTime agreedAt;           // 약관 동의 시간
    private String recipientName;
    private String recipientPhone;
    private String recipientEmail;
    private String recipientAddress;
}
