package com.project.korex.ForeignTransfer.service;

import com.project.korex.ForeignTransfer.dto.request.SenderTransferRequest;
import com.project.korex.ForeignTransfer.dto.response.SenderTransferResponse;
import com.project.korex.ForeignTransfer.entity.ForeignTransferTransaction;
import com.project.korex.ForeignTransfer.entity.Sender;
import com.project.korex.ForeignTransfer.repository.ForeignTransferTransactionRepository;
import com.project.korex.transaction.dto.response.ExchangeSimulationDto;
import com.project.korex.transaction.entity.Balance;
import com.project.korex.transaction.enums.AccountType;
import com.project.korex.transaction.repository.BalanceRepository;
import com.project.korex.transaction.service.ExchangeService;
import com.project.korex.user.entity.Users;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ForeignTransferService {

    private final UserJpaRepository userRepository;
    private final BalanceRepository balanceRepository;
    private final ForeignTransferTransactionRepository transactionRepository;
    private final ExchangeService exchangeService;

    // ────────────────────────────────
    // 송금 신청 단계 (환전 적용 포함)
    @Transactional
    public SenderTransferResponse createForeignTransfer(String loginId, SenderTransferRequest dto) {
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        BigDecimal transferAmount = dto.getTransferAmount();

        // 1️⃣ 원화 계좌 확인
        Balance krwBalance = balanceRepository.findByUserIdAndAccountType(user.getId(), AccountType.KRW)
                .orElseThrow(() -> new RuntimeException("원화 계좌가 없습니다."));

        BigDecimal convertedAmount = null;
        BigDecimal appliedRate = null;

        // 2️⃣ 환전 적용
        if (dto.getAccountType() == AccountType.FOREIGN && !"KRW".equals(dto.getCurrencyCode())) {
            // 환전 서비스 호출
            ExchangeSimulationDto simulation = exchangeService.simulateExchange(
                    "KRW", dto.getCurrencyCode(), transferAmount
            );

            appliedRate = simulation.getExchangeRate();
            convertedAmount = simulation.getToAmount(); // 여기서 환전 후 금액 가져오기

            // 잔액 차감 (KRW → 외화)
            if (krwBalance.getAvailableAmount().compareTo(transferAmount) < 0) {
                throw new RuntimeException("원화 잔액 부족");
            }
            krwBalance.setAvailableAmount(krwBalance.getAvailableAmount().subtract(transferAmount));
            krwBalance.setHeldAmount(krwBalance.getHeldAmount().add(transferAmount));

            // 외화 계좌에 환전 금액 추가
            Balance fxBalance = balanceRepository.findByUserIdAndCurrency_Code(user.getId(), dto.getCurrencyCode())
                    .orElseThrow(() -> new RuntimeException(dto.getCurrencyCode() + " 외화 계좌가 없습니다."));
            fxBalance.setAvailableAmount(fxBalance.getAvailableAmount().add(convertedAmount));
        }

        // 3️⃣ 송금 트랜잭션 생성
        ForeignTransferTransaction transaction = ForeignTransferTransaction.builder()
                .transferAmount(transferAmount)
                .accountPassword(dto.getAccountPassword())
                .relationRecipient(dto.getRelationRecipient())
                .user(user)
                .krwNumber(user.getKrwAccount())
                .foreignNumber(user.getForeignAccount())
                .requestStatus(ForeignTransferTransaction.RequestStatus.PENDING)
                .transferStatus(ForeignTransferTransaction.TransferStatus.NOT_STARTED)
                .createdAt(LocalDateTime.now())
                .build();

        Sender sender = Sender.builder()
                .name(dto.getSenderName())
                .transferReason(dto.getTransferReason())
                .foreignTransferTransaction(transaction)
                .build();

        transaction.setSender(sender);
        transactionRepository.save(transaction);

        // 4️⃣ DTO 반환
        return SenderTransferResponse.builder()
                .transferId(transaction.getId())
                .senderId(sender.getId())
                .accountType(dto.getAccountType().name())
                .availableBalance(krwBalance.getAvailableAmount())
                .transferAmount(transferAmount)
                .convertedAmount(convertedAmount)
                .appliedRate(appliedRate)
                .withdrawalMethod(dto.getWithdrawalMethod())
                .transferReason(dto.getTransferReason())
                .relationRecipient(dto.getRelationRecipient())
                .requestStatus(transaction.getRequestStatus().name())
                .transferStatus(transaction.getTransferStatus().name())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}