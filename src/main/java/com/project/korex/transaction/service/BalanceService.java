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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final CurrencyRepository currencyRepository;
    private final TransactionRepository transactionRepository;
    private final AuthService userService; // 현재 사용자 ID 조회용

    // 조회
    @Transactional(readOnly = true)
    public List<BalanceResponseDto> getMyBalances() {
        Long userId = userService.getCurrentUserId();
        return balanceRepository.findByUserId(userId).stream()
                .map(b -> BalanceResponse.builder()
                        .currencyCode(b.getCurrency().getCurrencyCode())
                        .availableAmount(b.getAvailableAmount())
                        .heldAmount(b.getHeldAmount())
                        .accountType(b.getAccountType() != null ? b.getAccountType().name() : null)
                        .currencyName(b.getCurrency().getCurrencyName())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public BalanceResponseDto getMyBalance(String currencyCode) {
        Long userId = userService.getCurrentUserId();
        Balance b = balanceRepository.findByUserIdAndCurrency_CurrencyCode(userId, currencyCode)
                .orElseThrow(ErrorCode.NOT_FOUND);
        return BalanceResponseDto.builder()
                .currencyCode(currencyCode)
                .availableAmount(b.getAvailableAmount())
                .heldAmount(b.getHeldAmount())
                .accountType(b.getAccountType()!=null? b.getAccountType().name():null)
                .currencyName(b.getCurrency().getCurrencyName())
                .build();
    }

    // 입금
//    public TransactionResponseDto deposit(DepositRequest req) {
//        Long userId = userService.getCurrentUserId();
//        Balance bal = lockOrCreate(userId, req.getCurrencyCode());
//        bal.increase(req.getAmount());
//        balanceRepository.save(bal);
//
//        Transaction tx = Transaction.builder()
//                .fromUserId(userId)
//                .toUserId(userId)
//                .fromCurrencyCode(req.getCurrencyCode())
//                .toCurrencyCode(req.getCurrencyCode())
//                .sendAmount(req.getAmount())
//                .receiveAmount(req.getAmount())
//                .transactionType(TransactionType.DEPOSIT)
//                .status(TransactionStatus.COMPLETED)
//                .createdAt(LocalDateTime.now())
//                .build();
//        tx = transactionRepository.save(tx);
//
//        return toTxResponse(tx, "입금 완료");
//    }

    // 출금
//    public TransactionResponse withdraw(WithdrawRequest req) {
//        Long userId = userService.getCurrentUserId();
//        Balance bal = lockOrCreate(userId, req.getCurrencyCode());
//        ensureSufficient(bal, req.getAmount());
//        bal.decrease(req.getAmount());
//        balanceRepository.save(bal);
//
//        Transaction tx = Transaction.builder()
//                .fromUserId(userId)
//                .toUserId(userId)
//                .fromCurrencyCode(req.getCurrencyCode())
//                .toCurrencyCode(req.getCurrencyCode())
//                .sendAmount(req.getAmount())
//                .receiveAmount(req.getAmount())
//                .transactionType(TransactionType.WITHDRAWAL)
//                .status(TransactionStatus.COMPLETED)
//                .createdAt(LocalDateTime.now())
//                .build();
//        tx = transactionRepository.save(tx);
//        return toTxResponse(tx, "출금 완료");
//    }

    // 송금/환전 처리 시 사용되는 유틸들

    @Transactional
    public boolean hasSufficientBalance(Long userId, String currencyCode, BigDecimal amount) {
        Balance bal = lockOrCreate(userId, currencyCode);
        return bal.getAvailableAmount().compareTo(amount) >= 0;
    }

    @Transactional
    public void processTransfer(Long senderId, Long recipientId,
                                String fromCode, String toCode,
                                BigDecimal sendAmount, BigDecimal receiveAmount,
                                BigDecimal feeAmount) {
        // 1) 송신자 출금 (수수료는 같은 통화로 차감 가정)
        Balance senderFrom = lockOrCreate(senderId, fromCode);
        BigDecimal totalDeduct = sendAmount.add(feeAmount==null? BigDecimal.ZERO : feeAmount);
        ensureSufficient(senderFrom, totalDeduct);
        senderFrom.decrease(totalDeduct);
        balanceRepository.save(senderFrom);

        // 2) 수신자 입금
        Balance recvTo = lockOrCreate(recipientId, toCode);
        recvTo.increase(receiveAmount);
        balanceRepository.save(recvTo);
    }

    // 교차 환전(외화→KRW→외화) 내부 처리에도 재사용 가능
    @Transactional
    public void processCrossExchange(Long userId,
                                     String fromCode, String toCode,
                                     BigDecimal fromAmount, // 보낼 외화 금액
                                     BigDecimal step1KrwAmount, // 외화→KRW 변환 결과
                                     BigDecimal step2ToAmount,  // KRW→외화 변환 결과
                                     BigDecimal feeInFrom) {    // 수수료(보내는 통화 기준)
        // from 통화 차감 (수수료 포함)
        Balance fromBal = lockOrCreate(userId, fromCode);
        ensureSufficient(fromBal, fromAmount.add(feeInFrom));
        fromBal.decrease(fromAmount.add(feeInFrom));
        balanceRepository.save(fromBal);

        // 중간 KRW 잔액 +step1 / -step1 후 +step2/-step2를 실제로 반영할 필요가 없을 수도 있음
        // 회계상 기록을 남기려면 내부 트랜잭션 행을 더 작성하고, 실계좌는 to 통화만 증가시키는 설계도 가능

        // 최종 to 통화 증가
        Balance toBal = lockOrCreate(userId, toCode);
        toBal.increase(step2ToAmount);
        balanceRepository.save(toBal);
    }

    // 내부 유틸
    private Balance lockOrCreate(Long userId, String currencyCode) {
        return balanceRepository.lockByUserAndCode(userId, currencyCode)
                .orElseGet(() -> {
                    Currency cur = currencyRepository.findById(currencyCode)
                            .orElseThrow(() -> new NotFoundException("통화 없음: " + currencyCode));
                    return balanceRepository.save(
                            Balance.builder()
                                    .userId(userId)
                                    .currency(cur)
                                    .availableAmount(BigDecimal.ZERO)
                                    .heldAmount(BigDecimal.ZERO)
                                    .accountType(AccountType.WALLET)
                                    .build()
                    );
                });
    }

    private void ensureSufficient(Balance bal, BigDecimal amount) {
        if (bal.getAvailableAmount().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("잔액 부족");
        }
    }

//    private TransactionResponseDto toTxResponse(Transaction tx, String msg) {
//        return TransactionResponse.builder()
//                .transactionId(tx.getTransactionId())
//                .status(tx.getStatus().name())
//                .transactionTime(tx.getCreatedAt())
//                .message(msg)
//                .build();
//    }
}

