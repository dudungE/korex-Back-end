package com.project.korex.transaction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponseDto {
    private boolean success;
    private String message;
    private TransferDataDto data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransferDataDto {
        private String transactionId;
        private String status;
        private String transferTime;
        private String message;
    }
}
