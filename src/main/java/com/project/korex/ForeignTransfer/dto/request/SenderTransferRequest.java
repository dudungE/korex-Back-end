// SenderTransferRequest.java
package com.project.korex.ForeignTransfer.dto.request;

import com.project.korex.transaction.enums.AccountType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SenderTransferRequest {
    private String senderName;
    private String accountPassword;
    private String transferReason;
    private String withdrawalMethod;
    private AccountType accountType;  // Enum으로 수정
    private String currencyCode;
    private BigDecimal transferAmount;
    private String relationRecipient;
}
