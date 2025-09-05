package com.project.korex.ForeignTransfer.service;

import com.project.korex.ForeignTransfer.dto.request.TransferExchangeRequest;
import com.project.korex.ForeignTransfer.dto.response.TransferExchangeResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferExchangeService {

    @Getter
    private BigDecimal feePercentage = new BigDecimal("0.01"); // 1%, 나중에 관리자 설정으로 변경 가능

    public void setFeePercentage(BigDecimal newFeePercentage) {
        if (newFeePercentage == null || newFeePercentage.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("수수료율이 올바르지 않습니다.");
        }
        this.feePercentage = newFeePercentage;
    }

    public TransferExchangeResponse simulateExchange(TransferExchangeRequest request, BigDecimal exchangeRate) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("송금 금액이 유효하지 않습니다.");
        }

        BigDecimal fromAmount = request.getAmount();
        BigDecimal toAmount = fromAmount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal fee = fromAmount.multiply(feePercentage).setScale(0, RoundingMode.HALF_UP);
        BigDecimal totalDeductedAmount = fromAmount.add(fee);

        return TransferExchangeResponse.builder()
                .fromAmount(fromAmount)
                .toAmount(toAmount)
                .exchangeRate(exchangeRate)
                .fee(fee)
                .totalDeductedAmount(totalDeductedAmount)
                .rateUpdateTime(LocalDateTime.now())
                .build();
    }
}
