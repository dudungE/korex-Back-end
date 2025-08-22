package com.project.korex.transaction.service;

import com.project.korex.common.code.ErrorCode;
import com.project.korex.common.exception.InsufficientBalanceException;
import com.project.korex.common.exception.UserNotFoundException;
import com.project.korex.exchangeRate.service.ExchangeRateCrawlerService;
import com.project.korex.transaction.dto.response.ExchangeCalculationDto;
import com.project.korex.transaction.dto.response.ExchangeResultDto;
import com.project.korex.transaction.dto.response.ExchangeSimulationDto;
import com.project.korex.transaction.entity.Balance;
import com.project.korex.transaction.entity.Currency;
import com.project.korex.transaction.entity.Transaction;
import com.project.korex.transaction.enums.TransactionType;
import com.project.korex.transaction.exception.ExchangeException;
import com.project.korex.transaction.repository.BalanceRepository;
import com.project.korex.transaction.repository.CurrencyRepository;
import com.project.korex.transaction.repository.TransactionRepository;
import com.project.korex.user.entity.Users;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.security.auth.login.AccountNotFoundException;
import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ExchangeService {

    private final CurrencyRepository currencyRepository;

    @Autowired
    private BalanceService balanceService; // 기존 BalanceService 활용

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserJpaRepository userRepository;

    // Redis 환율 조회는 RestTemplate으로 기존 API 호출
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 환전 시뮬레이션
     */
    public ExchangeSimulationDto simulateExchange(String fromCurrency, String toCurrency, BigDecimal amount) {
        // 1. 기존 ExchangeRateController API로 현재 환율 조회
        List<Map<String, String>> currentRate = getCurrentExchangeRateFromAPI(fromCurrency, toCurrency);
        if (currentRate == null || currentRate.isEmpty()) {
            throw new ExchangeException("환율 정보를 조회할 수 없습니다.");
        }

        // 2. 환전 계산
        ExchangeCalculationDto calculation = calculateExchange(fromCurrency, toCurrency, amount, currentRate.get(0));

        return ExchangeSimulationDto.builder()
                .fromAmount(amount)
                .toAmount(calculation.getConvertedAmount())
                .exchangeRate(calculation.getExchangeRate())
                .fee(calculation.getFee())
                .totalDeductedAmount(calculation.getTotalDeductedAmount())
                .rateUpdateTime(currentRate.get(0).get("crawl_time"))
                .build();
    }

    /**
     * 환전 실행
     */
    public ExchangeResultDto executeExchange(Long userId, String fromCurrency, String toCurrency, BigDecimal amount) {
        // 1. 사용자 존재 확인
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 2. Currency 객체 조회
        Currency fromCurrencyEntity = currencyRepository.findById(fromCurrency)
                .orElseThrow(() -> new ExchangeException(ErrorCode.INVALID_CURRENCY, "유효하지 않은 출금 통화: " + fromCurrency));

        Currency toCurrencyEntity = currencyRepository.findById(toCurrency)
                .orElseThrow(() -> new ExchangeException(ErrorCode.INVALID_CURRENCY, "유효하지 않은 입금 통화: " + toCurrency));

        // 2. 기존 API로 현재 환율 조회
        List<Map<String, String>> currentRate = getCurrentExchangeRateFromAPI(fromCurrency, toCurrency);
        if (currentRate == null || currentRate.isEmpty()) {
            throw new ExchangeException(ErrorCode.EXCHANGE_RATE_NOT_FOUND);
        }

        // 3. 환전 계산
        ExchangeCalculationDto calculation = calculateExchange(fromCurrency, toCurrency, amount, currentRate.get(0));

        // 4. 잔액 확인 (기존 BalanceService 활용)
        if (!balanceService.hasEnoughBalance(userId, fromCurrency, amount)) {
            throw new InsufficientBalanceException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        // 5. 환전 실행 (기존 BalanceService 활용)
        balanceService.deductBalance(userId, fromCurrency, amount);
        balanceService.addBalance(userId, toCurrency, calculation.getTotalDeductedAmount());

        // 6. 거래 기록 저장
        Transaction transaction = Transaction.builder()
                .fromUser(user)
                .toUser(user)
                .fromCurrencyCode(fromCurrencyEntity)
                .toCurrencyCode(toCurrencyEntity)
                .sendAmount(amount)
                .receiveAmount(calculation.getTotalDeductedAmount())
                .feePercentage(new BigDecimal("0.005"))
                .exchangeRateApplied(calculation.getExchangeRate())
                .feeAmount(calculation.getFee())
                .totalDeductedAmount(amount)
                .transactionType(TransactionType.EXCHANGE)
//                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        return ExchangeResultDto.builder()
                .success(true)
                .exchangeId(transaction.getId())
                .total_deducted_amount(calculation.getTotalDeductedAmount())
                .appliedRate(calculation.getExchangeRate())
                .fee(calculation.getFee())
                .build();
    }

    /**
     * 기존 ExchangeRateController API 호출로 환율 조회
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, String>> getCurrentExchangeRateFromAPI(String fromCurrency, String toCurrency) {
        try {
            String targetCurrency = "KRW".equals(fromCurrency) ? toCurrency : fromCurrency;

            String url = "http://localhost:8080/api/exchange/realtime/" + targetCurrency;
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);

            return response.getBody();
        } catch (Exception e) {
            log.error("환율 API 호출 오류: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 환전 계산
     */
    private ExchangeCalculationDto calculateExchange(String fromCurrency, String toCurrency,
                                                  BigDecimal amount, Map<String, String> currentRate) {

        BigDecimal baseRate = new BigDecimal(currentRate.get("base_rate").toString().replace(",", ""));

        BigDecimal convertedAmount;
        if ("KRW".equals(fromCurrency)) {
            convertedAmount = amount.divide(baseRate, 6, RoundingMode.HALF_UP);
        } else if ("KRW".equals(toCurrency)) {
            convertedAmount = amount.multiply(baseRate);
        } else {
            //throw new ExchangeException("KRW를 포함한 환전만 지원됩니다.");
        }

        BigDecimal feeRate = new BigDecimal("0.005");
        BigDecimal fee = amount.multiply(feeRate);

        BigDecimal afterFee;
        if ("KRW".equals(fromCurrency)) {
            BigDecimal amountAfterFee = amount.subtract(fee);
            afterFee = amountAfterFee.divide(baseRate, 6, RoundingMode.HALF_UP);
        } else {
            afterFee = convertedAmount.subtract(fee);
        }

        BigDecimal preferentialRate = new BigDecimal("1.10");
        BigDecimal finalAmount = afterFee.multiply(preferentialRate);

        return ExchangeCalculationDto.builder()
                .exchangeRate(baseRate)
                .convertedAmount(convertedAmount)
                .fee(fee)
                .totalDeductedAmount(finalAmount)
                .build();
    }

    /**
     * 환전 내역 조회
     */
    public List<Transaction> getExchangeHistory(Long userId, int page, int size) {
//        Pageable pageable = (Pageable) PageRequest.of(page, size);
//        return transactionRepository.findByUserIdAndTransactionTypeOrderByCreatedAtDesc(
//                userId, TransactionType.EXCHANGE, pageable).getContent();
        return null;
    }

    public boolean canExchange(String fromCurrency, String toCurrency) {
        return "KRW".equals(fromCurrency) || "KRW".equals(toCurrency);
    }
}



