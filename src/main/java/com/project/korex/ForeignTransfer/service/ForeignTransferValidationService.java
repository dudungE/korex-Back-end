package com.project.korex.ForeignTransfer.service;

import com.project.korex.ForeignTransfer.dto.request.MultiBalanceCheckRequest;
import com.project.korex.ForeignTransfer.dto.response.MultiBalanceCheckResponse;
import com.project.korex.transaction.entity.Balance;
import com.project.korex.transaction.entity.Currency;
import com.project.korex.transaction.enums.AccountType;
import com.project.korex.transaction.repository.BalanceRepository;
import com.project.korex.transaction.repository.CurrencyRepository;
import com.project.korex.user.entity.Users;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ForeignTransferValidationService {

    private final UserJpaRepository userRepository;
    private final BalanceRepository balanceRepository;
    private final CurrencyRepository currencyRepository;

    public MultiBalanceCheckResponse checkBalance(MultiBalanceCheckRequest request) {
        Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        Balance balance;
        if ("KRW".equals(request.getAccountType())) {
            // 원화 계좌는 accountType=KRW
            balance = balanceRepository.findByUserIdAndAccountType(
                            user.getId(), AccountType.KRW)
                    .orElseThrow(() -> new RuntimeException("원화 계좌 잔액 정보가 없습니다."));
        } else {
            // 외화 계좌는 currencyCode 필요
            Currency currency = currencyRepository.findById(request.getCurrencyCode())
                    .orElseThrow(() -> new RuntimeException(request.getCurrencyCode() + " 통화 정보가 없습니다."));
            balance = balanceRepository.findByUserIdAndCurrency(user.getId(), currency)
                    .orElseThrow(() -> new RuntimeException(request.getCurrencyCode() + " 잔액 정보가 없습니다."));
        }

        MultiBalanceCheckResponse response = new MultiBalanceCheckResponse();
        response.setAccountType(request.getAccountType());
        response.setCurrencyCode(balance.getCurrency().getCode());
        response.setAvailableAmount(balance.getAvailableAmount());

        return response;
    }
}
