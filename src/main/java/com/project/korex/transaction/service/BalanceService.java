package com.project.korex.transaction.service;

import com.project.korex.auth.service.AuthService;
import com.project.korex.common.code.ErrorCode;
import com.project.korex.common.exception.InsufficientBalanceException;
import com.project.korex.transaction.dto.response.BalanceResponseDto;
import com.project.korex.transaction.entity.Balance;
import com.project.korex.transaction.entity.Currency;
import com.project.korex.transaction.entity.Transaction;
import com.project.korex.transaction.enums.AccountType;
import com.project.korex.transaction.repository.BalanceRepository;
import com.project.korex.transaction.repository.CurrencyRepository;
import com.project.korex.transaction.repository.TransactionRepository;
import com.project.korex.user.entity.Users;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final CurrencyRepository currencyRepository;
    private final UserJpaRepository userRepository;

    @Transactional(readOnly = true)
    public List<BalanceResponseDto> getUserBalances(Long userId) {
        List<Balance> balances = balanceRepository.findByUserId(userId);

        return balances.stream()
                .map(balance -> BalanceResponseDto.from(balance, balance.getCurrency()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean hasEnoughBalance(Long userId, String currencyCode, BigDecimal amount) {
        // Currency 객체로 조회
        Currency currency = currencyRepository.findById(currencyCode)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 통화 코드: " + currencyCode));

        Optional<Balance> balance = balanceRepository.findByUserIdAndCurrency(userId, currency);
        return balance.map(b -> b.getAvailableAmount().compareTo(amount) >= 0).orElse(false);
    }

    public void deductBalance(Long userId, String currencyCode, BigDecimal amount) {
        Currency currency = currencyRepository.findById(currencyCode)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 통화 코드: " + currencyCode));

        Balance balance = balanceRepository.findByUserIdAndCurrency(userId, currency)
                .orElseThrow(() -> new InsufficientBalanceException(ErrorCode.INSUFFICIENT_BALANCE));

        if (balance.getAvailableAmount().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        balance.setAvailableAmount(balance.getAvailableAmount().subtract(amount));
//        balance.setHeldAmount(balance.getHeldAmount().subtract(amount));
        balanceRepository.save(balance);
    }

    public void addBalance(Long userId, String currencyCode, BigDecimal amount) {
        Currency currency = currencyRepository.findById(currencyCode)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 통화 코드: " + currencyCode));

        Optional<Balance> existingBalance = balanceRepository.findByUserIdAndCurrency(userId, currency);

        if (existingBalance.isPresent()) {
            Balance balance = existingBalance.get();
            balance.setAvailableAmount(balance.getAvailableAmount().add(amount));
//            balance.setHeldAmount(balance.getHeldAmount().add(amount));
            balanceRepository.save(balance);
        } else {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));
            // 새 잔액 생성
            Balance newBalance = new Balance();
            newBalance.setUser(user);
            newBalance.setCurrency(currency);
            newBalance.setAvailableAmount(amount);
//            newBalance.setHeldAmount(amount);

            // 통화코드에 따라 AccountType 결정
            if ("KRW".equals(currencyCode)) {
                newBalance.setAccountType(AccountType.KRW);
            } else {
                newBalance.setAccountType(AccountType.FOREIGN);
            }

            balanceRepository.save(newBalance);
        }
    }

}

