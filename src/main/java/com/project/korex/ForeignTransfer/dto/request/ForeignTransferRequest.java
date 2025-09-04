package com.project.korex.ForeignTransfer.dto.request;

import com.project.korex.transaction.enums.AccountType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
public class ForeignTransferRequest {

    // 송금 금액/계좌
    private BigDecimal transferAmount;
    private AccountType accountType;
    private String currencyCode;
    private String accountPassword;
    private String krwAccount;
    private String foreignAccount;

    // 송금인 정보
    private String senderName;
    private String transferReason;
    private String countryNumber;
    private String phoneNumber;
    private String email;
    private String country;
    private String engAddress;
    private String relationRecipient;
    private String accountNumber;
    private String withdrawalMethod;
    private String staffMessage;

    // 파일
    private MultipartFile idFile;
    private MultipartFile proofDocumentFile;
    private MultipartFile relationDocumentFile;

    // 약관 동의
    private boolean agree1;
    private boolean agree2;
    private boolean agree3;

}
