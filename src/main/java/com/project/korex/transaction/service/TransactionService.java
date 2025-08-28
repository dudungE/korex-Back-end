package com.project.korex.transaction.service;

import com.project.korex.transaction.dto.response.TransactionResponseDto;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 특정 사용자 간 마지막 송금일 조회 (성능 최적화)
     */
    @Transactional(readOnly = true)
    public LocalDateTime getLastTransferDate(Long fromUserId, Long toUserId) {
        return transactionRepository
                .findLastTransferDate(fromUserId, toUserId, TransactionType.TRANSFER)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getUserTransactions(Long userId) {
        List<Transaction> transactions = transactionRepository.findByFromUserIdOrToUserIdOrderByCreatedAtDesc(userId, userId);

        return transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private TransactionResponseDto convertToDto(Transaction transaction) {
        return TransactionResponseDto.builder()
                .id(transaction.getId())
                .fromUserId(transaction.getFromUser() != null ? transaction.getFromUser().getId() : null)
                .fromUserName(transaction.getFromUser() != null ? transaction.getFromUser().getName() : null)
                .toUserId(transaction.getToUser() != null ? transaction.getToUser().getId() : null)
                .toUserName(transaction.getToUser() != null ? transaction.getToUser().getName() : null)
                .fromCurrencyCode(transaction.getFromCurrencyCode() != null ? transaction.getFromCurrencyCode().getCode() : null)
                .fromCurrencyName(transaction.getFromCurrencyCode() != null ? transaction.getFromCurrencyCode().getCurrencyName() : null)
                .toCurrencyCode(transaction.getToCurrencyCode() != null ? transaction.getToCurrencyCode().getCode() : null)
                .toCurrencyName(transaction.getToCurrencyCode() != null ? transaction.getToCurrencyCode().getCurrencyName() : null)
                .sendAmount(transaction.getSendAmount())
                .receiveAmount(transaction.getReceiveAmount())
                .feeAmount(transaction.getFeeAmount())
                .totalDeductedAmount(transaction.getTotalDeductedAmount())
                .transactionType(transaction.getTransactionType())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

}

