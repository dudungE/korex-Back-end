package com.project.korex.ForeignTransfer.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BalanceCheckRequest {
    private String accountType; // "KRW" or "FX"
    private String currencyCode; // FX일 때 필수
}