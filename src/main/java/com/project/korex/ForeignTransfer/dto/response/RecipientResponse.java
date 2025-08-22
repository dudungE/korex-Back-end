package com.project.korex.ForeignTransfer.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RecipientResponse {

    private Long recipientId;
    private String name;
    private String bankName;
    private String accountNumber;
    private String countryNumber;
    private String country;
    private String phoneNumber;
    private String email;
    private String relationRecipient;
    private String currency;
    private String engAddress;

}
