package com.project.korex.transaction.service;

import com.project.korex.transaction.dto.response.TransactionResponseDto;
import com.project.korex.transaction.dto.response.MonthlyStatsDto;
import com.project.korex.transaction.entity.Currency;
import com.project.korex.transaction.entity.Transaction;
import com.project.korex.transaction.enums.TransactionType;
import com.project.korex.transaction.repository.CurrencyRepository;
import com.project.korex.transaction.repository.TransactionRepository;
import com.project.korex.user.entity.Users;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserJpaRepository userRepository;
    private final CurrencyRepository currencyRepository;

    public Transaction createTransaction(Users fromUser, Users toUser,
                                         String fromCurrencyCode, String toCurrencyCode,
                                         BigDecimal sendAmount, BigDecimal receiveAmount,
                                         BigDecimal feeAmount, TransactionType transactionType) {

        Currency fromCurrency = currencyRepository.findById(fromCurrencyCode)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 송금 통화 코드: " + fromCurrencyCode));

        Currency toCurrency = currencyRepository.findById(toCurrencyCode)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 수취 통화 코드: " + toCurrencyCode));

        Transaction transaction = new Transaction();
        transaction.setFromUser(fromUser);
        transaction.setToUser(toUser);
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
    public LocalDateTime getLastTransferDate(Long fromUserId, Long toUserId) {
        return transactionRepository
                .findLastTransferDate(fromUserId, toUserId, TransactionType.TRANSFER)
                .orElse(null);
    }

    /**
     * 기본 거래 내역 조회 - 필터링 없음 (다른 곳에서 사용)
     */
    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getUserTransactions(Long userId) {
        List<Transaction> transactions = transactionRepository
                .findByUserIdOrderByCreatedAtDesc(userId);

        return transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 필터링이 적용된 거래 내역 조회 (Controller에서 사용)
     */
    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getUserTransactions(Long userId, String currencyCode,
                                                            String period, String type, String sortBy) {

        List<Transaction> transactions;

        if (currencyCode != null && !currencyCode.isEmpty() && !"all".equals(currencyCode)) {
            Currency currency = currencyRepository.findById(currencyCode)
                    .orElseThrow(() -> new RuntimeException("유효하지 않은 통화 코드: " + currencyCode));

            transactions = transactionRepository.findByUserIdAndCurrency(userId, currency);
        } else {
            transactions = transactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }

        transactions = filterByPeriod(transactions, period);
        transactions = filterByType(transactions, type, userId);
        transactions = sortTransactions(transactions, sortBy);

        return transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    /**
     * 특정 통화의 거래 내역만 조회 (환전 등에서 사용)
     */
    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getUserTransactionsByCurrency(Long userId, String currencyCode) {
        Currency currency = currencyRepository.findById(currencyCode)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 통화 코드: " + currencyCode));

        List<Transaction> transactions = transactionRepository.findByUserIdAndCurrency(userId, currency);

        return transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 통화의 월간 통계 조회
     */
    @Transactional(readOnly = true)
    public MonthlyStatsDto getMonthlyStats(Long userId, String currencyCode) {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        Currency currency = currencyRepository.findById(currencyCode)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 통화 코드: " + currencyCode));

        List<Transaction> monthlyTransactions = transactionRepository
                .findMonthlyTransactionsByCurrency(userId, currency, startOfMonth, endOfMonth);

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;
        long count = monthlyTransactions.size();

        for (Transaction transaction : monthlyTransactions) {
            if (transaction.getToUser() != null && transaction.getToUser().getId().equals(userId)) {
                income = income.add(transaction.getReceiveAmount() != null ? transaction.getReceiveAmount() : BigDecimal.ZERO);
            } else if (transaction.getFromUser() != null && transaction.getFromUser().getId().equals(userId)) {
                expense = expense.add(transaction.getSendAmount() != null ? transaction.getSendAmount() : BigDecimal.ZERO);
            }
        }

        return MonthlyStatsDto.builder()
                .income(income)
                .expense(expense)
                .count(count)
                .build();
    }

    /**
     * 특정 기간의 거래 내역 조회
     */
    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getTransactionsByPeriod(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = transactionRepository
                .findTransactionsByUserIdAndPeriod(userId, startDate, endDate);

        return transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 거래 타입의 거래 내역 조회
     */
    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getTransactionsByType(Long userId, TransactionType transactionType) {
        List<Transaction> transactions = transactionRepository
                .findByUserIdAndTransactionType(userId, transactionType);

        return transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private List<Transaction> filterByPeriod(List<Transaction> transactions, String period) {
        if (period == null || "all".equals(period)) {
            return transactions;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime filterDate;

        switch (period) {
            case "week":
                filterDate = now.minusWeeks(1);
                break;
            case "month":
                filterDate = now.minusMonths(1);
                break;
            case "3months":
                filterDate = now.minusMonths(3);
                break;
            case "6months":
                filterDate = now.minusMonths(6);
                break;
            case "year":
                filterDate = now.minusYears(1);
                break;
            default:
                return transactions;
        }

        return transactions.stream()
                .filter(t -> t.getCreatedAt() != null && t.getCreatedAt().isAfter(filterDate))
                .collect(Collectors.toList());
    }

    private List<Transaction> filterByType(List<Transaction> transactions, String type, Long userId) {
        if (type == null || "all".equals(type)) {
            return transactions;
        }

        return transactions.stream()
                .filter(t -> {
                    if ("income".equals(type)) {
                        return t.getToUser() != null && t.getToUser().getId().equals(userId);
                    } else if ("expense".equals(type)) {
                        return t.getFromUser() != null && t.getFromUser().getId().equals(userId);
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    private List<Transaction> sortTransactions(List<Transaction> transactions, String sortBy) {
        if (sortBy == null || "date".equals(sortBy)) {
            return transactions.stream()
                    .sorted((a, b) -> {
                        if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                        if (a.getCreatedAt() == null) return 1;
                        if (b.getCreatedAt() == null) return -1;
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    })
                    .collect(Collectors.toList());
        } else if ("amount".equals(sortBy)) {
            return transactions.stream()
                    .sorted((a, b) -> {
                        BigDecimal amountA = a.getSendAmount() != null ? a.getSendAmount() : BigDecimal.ZERO;
                        BigDecimal amountB = b.getSendAmount() != null ? b.getSendAmount() : BigDecimal.ZERO;
                        return amountB.compareTo(amountA);
                    })
                    .collect(Collectors.toList());
        }
        return transactions;
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
