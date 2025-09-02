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
    private Boolean agreed;       // 엔티티에서 isAgreed 필드 포함
    private LocalDateTime agreedAt;
}
