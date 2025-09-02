package com.project.korex.externalAccount.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalAccountResponseDto {
    private Long accountId;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private BigDecimal balance;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
}
