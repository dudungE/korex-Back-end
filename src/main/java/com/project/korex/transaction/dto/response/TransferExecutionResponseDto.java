package com.project.korex.transaction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferExecutionResponseDto {

    private Long transactionId;
    private String transactionIdFormatted;  // "TX20250818001" 형태
    private String status;                  // "COMPLETED", "PENDING", "FAILED"
    private LocalDateTime transferTime;
    private String message;

}
