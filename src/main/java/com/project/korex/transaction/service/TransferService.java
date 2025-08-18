package com.project.korex.transaction.service;

import com.project.korex.auth.service.AuthService;
import com.project.korex.common.code.ErrorCode;
import com.project.korex.common.exception.UserNotFoundException;
import com.project.korex.exchangeRate.service.ExchangeRateService;
import com.project.korex.transaction.dto.request.TransferExecutionRequestDto;
import com.project.korex.transaction.dto.request.TransferCalculationRequestDto;
import com.project.korex.transaction.dto.response.CrossRateDetailsDto;
import com.project.korex.transaction.dto.response.RecipientResponseDto;
import com.project.korex.transaction.dto.response.TransferCalculationResponseDto;
import com.project.korex.transaction.dto.response.TransferExecutionResponseDto;
import com.project.korex.transaction.entity.Transaction;
import com.project.korex.transaction.enums.TransactionType;
import com.project.korex.transaction.repository.TransactionRepository;
import com.project.korex.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TransferService {

    private final TransactionRepository transactionRepository;
    private final BalanceService balanceService;
    private final ExchangeRateService exchangeRateService;
    private final AuthService userService;
    private final PasswordEncoder passwordEncoder;

    // 송금 계산
    // 송금 계산 (한국 규정 적용)
    public TransferCalculationResponseDto calculateTransfer(TransferCalculationRequestDto request) {

        String fromCurrency = request.getFromCurrencyCode();
        String toCurrency = request.getToCurrencyCode();
        BigDecimal sendAmount = request.getSendAmount();

        // 실시간 환율 조회
        BigDecimal exchangeRate = exchangeRateService.getTransferRate(fromCurrency, toCurrency);

        // 받을 금액 계산
        BigDecimal receiveAmount = sendAmount.multiply(exchangeRate)
                .setScale(2, RoundingMode.HALF_UP);

        // 수수료 계산 (교차 거래시 추가 수수료)
        BigDecimal feeAmount = calculateTransferFee(fromCurrency, toCurrency, sendAmount);

        // 총 차감 금액
        BigDecimal totalDeducted = sendAmount.add(feeAmount);

        // 거래 타입 결정
        String transferType = determineTransferType(fromCurrency, toCurrency);

        return TransferCalculationResponseDto.builder()
                .sendAmount(sendAmount)
                .receiveAmount(receiveAmount)
                .exchangeRateApplied(exchangeRate)
                .feeAmount(feeAmount)
                .totalDeductedAmount(totalDeducted)
//                .transferType(transferType)
//                .calculatedAt(LocalDateTime.now())
                .crossRateDetailsDto(getCrossRateDetails(fromCurrency, toCurrency)) // 교차 환율 상세
                .build();
    }

    // 송금 수수료 계산 (교차 거래 고려)
    private BigDecimal calculateTransferFee(String fromCurrency, String toCurrency, BigDecimal amount) {
        if (fromCurrency.equals(toCurrency)) {
            // 같은 통화 송금: 무료
            return BigDecimal.ZERO;
        }

        if ("KRW".equals(fromCurrency) || "KRW".equals(toCurrency)) {
            // 직접 거래 (KRW 포함): 0.1% 수수료
            return amount.multiply(new BigDecimal("0.001"))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // 교차 거래 (외화 → 외화): 0.2% 수수료 (2번 거래하므로)
        return amount.multiply(new BigDecimal("0.002"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    // 거래 타입 결정
    private String determineTransferType(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return "DIRECT";
        } else if ("KRW".equals(fromCurrency) || "KRW".equals(toCurrency)) {
            return "EXCHANGE";
        } else {
            return "CROSS_EXCHANGE"; // 교차 환전
        }
    }

    // 교차 환율 상세 정보
    private CrossRateDetailsDto getCrossRateDetails(String fromCurrency, String toCurrency) {
        if ("KRW".equals(fromCurrency) || "KRW".equals(toCurrency) || fromCurrency.equals(toCurrency)) {
            return null; // 직접 거래는 상세 정보 없음
        }

        BigDecimal fromToKrw = exchangeRateService.getTransferRate(fromCurrency, "KRW");
        BigDecimal krwToTo = exchangeRateService.getTransferRate("KRW", toCurrency);

        return CrossRateDetailsDto.builder()
                .step1Rate(fromToKrw)           // USD → KRW
                .step1Description(fromCurrency + " → KRW")
                .step2Rate(krwToTo)             // KRW → EUR
                .step2Description("KRW → " + toCurrency)
                .finalRate(fromToKrw.multiply(krwToTo))
                .build();
    }

    // 수취인 검색
    public RecipientResponseDto findRecipientByPhone(String phone) {
        Optional<Users> recipient = userService.findByPhone(phone);

        if (recipient.isEmpty()) {
            return RecipientResponseDto.builder()
                    .exists(false)
                    .phone(phone)
                    .build();
        }

        Users user = recipient.get();
        boolean isFriend = userService.isFriend(getCurrentUserId(), user.getId());

        return RecipientResponseDto.builder()
                .userId(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .exists(true)
                .isFriend(isFriend)
                .build();
    }

    // 송금 실행
    public TransferExecutionResponseDto executeTransfer(TransferExecutionRequestDto request) {
        // 1. 현재 사용자 조회
        Users sender = userService.getCurrentUser();

        // 2. 거래 비밀번호 검증 (4자리)
        if (!isValidTransactionPassword(sender, request.getTransactionPassword())) {
            throw new InvalidPasswordException("거래 비밀번호가 올바르지 않습니다.");
        }

        // 3. 수취인 확인
        Users recipient = userService.findByPhone(request.getRecipientPhone())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 4. 잔액 확인
        if (!balanceService.hasSufficientBalance(sender.getId(),
                request.getFromCurrencyCode(), request.getSendAmount())) {
            throw new InsufficientBalanceException("잔액이 부족합니다.");
        }

        // 5. 송금 계산 재수행 (최신 환율 적용)
        TransferCalculationResponseDto calculation = calculateTransfer(
                new TransferCalculationRequestDto(
                        request.getFromCurrencyCode(),
                        request.getToCurrencyCode(),
                        request.getSendAmount()
                )
        );

        // 6. 거래 생성
        Transaction transaction = createTransaction(sender, recipient, request, calculation);
        transaction = transactionRepository.save(transaction);

        // 7. 잔액 업데이트
        balanceService.processTransfer(
                sender.getId(), recipient.getId(),
                request.getFromCurrencyCode(), request.getToCurrencyCode(),
                calculation.getSendAmount(), calculation.getReceiveAmount(),
                calculation.getFeeAmount()
        );

        // 8. 응답 생성
        return TransferExecutionResponseDto.builder()
                .transactionId(transaction.getId())
                .transactionIdFormatted(formatTransactionId(transaction.getId()))
                .status("COMPLETED")
                .transferTime(transaction.getCreatedAt())
                .message("송금이 성공적으로 완료되었습니다.")
//                .transferDetail(TransferExecutionResponse.TransferDetail.builder()
//                        .recipientName(recipient.getName())
//                        .recipientPhone(recipient.getPhone())
//                        .sendAmount(calculation.getSendAmount())
//                        .receiveAmount(calculation.getReceiveAmount())
//                        .fromCurrency(request.getFromCurrencyCode())
//                        .toCurrency(request.getToCurrencyCode())
//                        .exchangeRate(calculation.getExchangeRateApplied())
//                        .feeAmount(calculation.getFeeAmount())
//                        .totalDeducted(calculation.getTotalDeductedAmount())
//                        .build())
                .build();
    }

    // 4자리 거래 비밀번호 검증
    private boolean isValidTransactionPassword(Users user, String inputPassword) {
        // 실제로는 암호화된 비밀번호와 비교
        return passwordEncoder.matches(inputPassword, user.getTransactionPassword());
    }

    // 거래 생성
    private Transaction createTransaction(Users sender, Users recipient,
                                          TransferExecutionRequestDto request,
                                          TransferCalculationResponseDto calculation) {
        return Transaction.builder()
                .fromUser(sender.getId())
                .toUser(recipient.getId())
                .fromCurrencyCode(request.getFromCurrencyCode())
                .toCurrencyCode(request.getToCurrencyCode())
                .sendAmount(calculation.getSendAmount())
                .receiveAmount(calculation.getReceiveAmount())
                .exchangeRateApplied(calculation.getExchangeRateApplied())
                .feeAmount(calculation.getFeeAmount())
                .totalDeductedAmount(calculation.getTotalDeductedAmount())
                .transactionType(TransactionType.TRANSFER)
                .status(calculation.getStatus())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 거래 ID 포맷팅
    private String formatTransactionId(Long transactionId) {
        return String.format("TX%s%04d",
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                transactionId % 10000);
    }

    private Long getCurrentUserId() {
        // 실제로는 Spring Security에서 현재 사용자 ID 조회
        return 1L; // 임시
    }
}
