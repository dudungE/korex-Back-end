package com.project.korex.ForeignTransfer.service;

import com.project.korex.ForeignTransfer.dto.request.SenderTransferRequest;
import com.project.korex.ForeignTransfer.dto.response.SenderTransferResponse;
import com.project.korex.ForeignTransfer.entity.ForeignTransferTransaction;
import com.project.korex.ForeignTransfer.entity.Sender;
import com.project.korex.transaction.entity.Balance;
import com.project.korex.transaction.repository.BalanceRepository;
import com.project.korex.ForeignTransfer.repository.ForeignTransferTransactionRepository;
import com.project.korex.user.entity.Users;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ForeignTransferService {

    private final UserJpaRepository userRepository;
    private final BalanceRepository balanceRepository;
    private final ForeignTransferTransactionRepository transactionRepository;

    @Transactional
    public SenderTransferResponse createForeignTransfer(SenderTransferRequest dto) {

        // 1️⃣ 사용자 조회
        Users user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 2️⃣ 잔액 조회
        List<Balance> balances = balanceRepository.findByUserId(user.getId());

        Balance balance = balances.stream()
                .filter(b -> b.getAccountType().name().equals(dto.getAccountType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("잔액 정보가 없습니다."));

        BigDecimal availableBalance = balance.getAvailableAmount();

        // 3️⃣ 잔액 체크
        if (availableBalance.compareTo(dto.getTransferAmount()) < 0) {
            throw new RuntimeException("잔액이 부족합니다.");
        }

// 4️⃣ 잔액 차감
        balanceRepository.deductBalance(
                user.getId(),
                balance.getCurrency().getCode(), // Currency code 사용
                dto.getTransferAmount()
        );

        // 5️⃣ Sender 생성
        Sender sender = Sender.builder()
                .user(user)
                .transferReason(dto.getTransferReason())
                .relationRecipient(dto.getRelationRecipient())
                .build();

        // 6️⃣ ForeignTransferTransaction 생성
        ForeignTransferTransaction transaction = ForeignTransferTransaction.builder()
                .user(user)
                .transferAmount(dto.getTransferAmount())
                .feeAmount(BigDecimal.ZERO)
                .requestStatus(ForeignTransferTransaction.RequestStatus.NOT_STARTED)
                .transferStatus(ForeignTransferTransaction.TransferStatus.NOT_STARTED)
                .accountPassword(dto.getAccountPassword())
                .createdAt(LocalDateTime.now())
                .build();

        // 7️⃣ 양방향 연결
        transaction.setSender(sender);
        sender.setForeignTransferTransaction(transaction);

        // 8️⃣ 저장
        transactionRepository.save(transaction);

        // 9️⃣ Response DTO 생성
        SenderTransferResponse response = new SenderTransferResponse();
        response.setTransferId(transaction.getId());
        response.setSenderId(sender.getId());
        response.setAccountType(dto.getAccountType());
        response.setAvailableBalance(availableBalance.subtract(dto.getTransferAmount()));
        response.setTransferAmount(dto.getTransferAmount());
        response.setWithdrawalMethod(dto.getWithdrawalMethod());
        response.setTransferReason(dto.getTransferReason());
        response.setRelationRecipient(dto.getRelationRecipient());
        response.setRequestStatus(transaction.getRequestStatus().name());
        response.setTransferStatus(transaction.getTransferStatus().name());
        response.setCreatedAt(transaction.getCreatedAt());

        return response;
    }
}
