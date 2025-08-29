package com.project.korex.ForeignTransfer.dto.request;

import lombok.Data;

@Data
public class TermsAgreeRequest {
    private Long transferId; // 송금 신청 ID
    private Boolean agree1;
    private Boolean agree2;
    private Boolean agree3;
}
