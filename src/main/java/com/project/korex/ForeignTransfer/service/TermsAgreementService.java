package com.project.korex.ForeignTransfer.service;

import com.project.korex.ForeignTransfer.dto.request.TermsAgreeRequest;
import com.project.korex.ForeignTransfer.dto.response.TermsAgreementResponse;
import com.project.korex.ForeignTransfer.entity.ForeignTransferTransaction;
import com.project.korex.ForeignTransfer.entity.TermsAgreement;
import com.project.korex.ForeignTransfer.repository.ForeignTransferTransactionRepository;
import com.project.korex.ForeignTransfer.repository.TermsAgreementRepository;
import com.project.korex.transaction.entity.Transaction;
import com.project.korex.transaction.enums.TransactionType;
import com.project.korex.transaction.repository.TransactionRepository;
import com.project.korex.user.entity.Users;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TermsAgreementService {

    private final TermsAgreementRepository agreementRepository;
    private final ForeignTransferTransactionRepository transactionRepository;
    private final UserJpaRepository userRepository;
    private final TransactionRepository generalTransactionRepository; // 일반 Transaction

    @Transactional
    public TermsAgreementResponse agreeTermsAndCreateTransaction(TermsAgreeRequest request, Long userId) {
        // 1. 유저 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 2. 일반 Transaction 생성
        Transaction generalTransaction = Transaction.builder()
                .fromUser(user)
                .toUser(user) // 초기에는 자기 자신
                .transactionType(TransactionType.TRANSFER)
                .status("PENDING")
                .build();
        generalTransactionRepository.save(generalTransaction);

        // 4. ForeignTransferTransaction 생성
        ForeignTransferTransaction transaction = ForeignTransferTransaction.builder()
                .transaction(generalTransaction)
                .user(user)
                .createdAt(LocalDateTime.now())
                .requestStatus(ForeignTransferTransaction.RequestStatus.NOT_STARTED)
                .transferStatus(ForeignTransferTransaction.TransferStatus.NOT_STARTED)
                .bankName("KOREX BANK")
                .krwNumber(user.getKrwAccount())
                .foreignNumber(user.getForeignAccount())
                .accountPassword(user.getAccountPassword())
                .build();

        transactionRepository.save(transaction); // DB 저장

        // 5. TermsAgreement 생성
        TermsAgreement agreement = new TermsAgreement();
        agreement.setForeignTransferTransaction(transaction);
        agreement.setAgree1(request.getAgree1());
        agreement.setAgree2(request.getAgree2());
        agreement.setAgree3(request.getAgree3());
        agreement.setAgreed(Boolean.TRUE.equals(request.getAgree1()) && Boolean.TRUE.equals(request.getAgree2()));
        agreement.setAgreedAt(LocalDateTime.now());

        transaction.setTermsAgreement(agreement); // 양방향 연동
        agreementRepository.save(agreement);

        // 6. DTO 반환
        return toResponse(agreement);
    }

    public TermsAgreementResponse toResponse(TermsAgreement entity) {
        TermsAgreementResponse dto = new TermsAgreementResponse();
        dto.setId(entity.getId());
        dto.setTransferId(entity.getForeignTransferTransaction().getId());
        dto.setAgree1(entity.getAgree1());
        dto.setAgree2(entity.getAgree2());
        dto.setAgree3(entity.getAgree3());
        dto.setAgreedAt(entity.getAgreedAt());
        return dto;
    }
}