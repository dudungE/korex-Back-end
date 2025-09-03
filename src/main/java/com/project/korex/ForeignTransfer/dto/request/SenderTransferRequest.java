package com.project.korex.ForeignTransfer.dto.request;

import com.project.korex.transaction.enums.AccountType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class SenderTransferRequest {
    private String senderName;
    private String accountPassword;
    private String transferReason;
    private String withdrawalMethod;
    private AccountType accountType;
    private String currencyCode;
    private BigDecimal transferAmount;
    private String relationRecipient;

    private String countryNumber;
    private String phoneNumber;
    private String email;
    private String country;
    private String engAddress;
    private String staffMessage;

    private MultipartFile idFile;
    private MultipartFile proofDocumentFile;
    private MultipartFile relationDocumentFile;

    private String accountNumber; // 선택 계좌
}
