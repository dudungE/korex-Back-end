package com.project.korex.transaction.service;

import com.project.korex.transaction.entity.Currency;
import com.project.korex.transaction.entity.Transaction;
import com.project.korex.transaction.enums.TransactionType;
import com.project.korex.transaction.repository.CurrencyRepository;
import com.project.korex.transaction.repository.TransactionRepository;
import com.project.korex.user.entity.Users;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserJpaRepository userRepository;
    private final CurrencyRepository currencyRepository;

    public Transaction createTransaction(Users fromUser, Users toUser,
                                         String fromCurrencyCode, String toCurrencyCode,
                                         BigDecimal sendAmount, BigDecimal receiveAmount,
                                         BigDecimal feeAmount, TransactionType transactionType) {

        // Currency 엔티티 조회
        Currency fromCurrency = currencyRepository.findById(fromCurrencyCode)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 송금 통화 코드: " + fromCurrencyCode));

        Currency toCurrency = currencyRepository.findById(toCurrencyCode)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 수취 통화 코드: " + toCurrencyCode));

        Transaction transaction = new Transaction();
        transaction.setFromUser(fromUser);
        transaction.setToUser(toUser);

        // Currency 객체 설정
        transaction.setFromCurrencyCode(fromCurrency);
        transaction.setToCurrencyCode(toCurrency);

        transaction.setSendAmount(sendAmount);
        transaction.setReceiveAmount(receiveAmount);
        transaction.setExchangeRateApplied(BigDecimal.ONE);
        transaction.setFeeAmount(feeAmount);
        transaction.setTotalDeductedAmount(sendAmount.add(feeAmount));
        transaction.setTransactionType(transactionType);
        transaction.setStatus("COMPLETED");

        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getUserTransactions(Long userId) {
        return transactionRepository.findByFromUserIdOrToUserIdOrderByCreatedAtDesc(userId, userId);
    }
}

