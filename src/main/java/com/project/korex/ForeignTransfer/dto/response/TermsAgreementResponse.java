package com.project.korex.ForeignTransfer.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TermsAgreementResponse {
    private Long id;
    private Long transferId;
    private Boolean agree1;
    private Boolean agree2;
    private Boolean agree3;
    private LocalDateTime agreedAt;
}
