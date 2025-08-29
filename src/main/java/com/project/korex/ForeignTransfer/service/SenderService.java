package com.project.korex.ForeignTransfer.service;

import com.project.korex.ForeignTransfer.dto.request.SenderRequest;
import com.project.korex.ForeignTransfer.dto.response.SenderResponse;
import com.project.korex.ForeignTransfer.entity.ForeignTransferTransaction;
import com.project.korex.ForeignTransfer.entity.Sender;
import com.project.korex.ForeignTransfer.repository.ForeignTransferTransactionRepository;
import com.project.korex.ForeignTransfer.repository.SenderRepository;
import com.project.korex.transaction.entity.Transaction;
import com.project.korex.transaction.enums.TransactionType;
import com.project.korex.transaction.repository.TransactionRepository;
import com.project.korex.user.entity.Users;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SenderService {

    private final SenderRepository senderRepository;
    private final ForeignTransferTransactionRepository transactionRepository;
    private final UserJpaRepository userRepository;
    private final FileUploadService fileUploadService; // 파일 저장 서비스
    private final TransactionRepository generalTransactionRepository;

    @Transactional
    public SenderResponse createSenderWithTransaction(SenderRequest request, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 1. 일반 Transaction 생성
        Transaction generalTransaction = Transaction.builder()
                .fromUser(user)
                .toUser(user)
                .transactionType(TransactionType.TRANSFER)
                .status("PENDING")
                .build();
        generalTransactionRepository.save(generalTransaction);

        // 2. ForeignTransferTransaction 생성
        ForeignTransferTransaction transaction = ForeignTransferTransaction.builder()
                .transaction(generalTransaction)
                .user(user)
                .requestStatus(ForeignTransferTransaction.RequestStatus.PENDING)
                .transferStatus(ForeignTransferTransaction.TransferStatus.NOT_STARTED)
                .bankName("KOREX BANK")
                .krwNumber(user.getKrwAccount())
                .foreignNumber(user.getForeignAccount())
                .accountPassword(user.getAccountPassword())
                .build();
        transactionRepository.save(transaction);

        // 3. Sender 생성 + 파일 업로드
        Sender sender = Sender.builder()
                .user(user)
                .name(request.getName())
                .transferReason(request.getTransferReason())
                .countryNumber(request.getCountryNumber())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .country(request.getCountry())
                .engAddress(request.getEngAddress())
                .staffMessage(request.getStaffMessage())
                .relationRecipient(request.getRelationRecipient())
                .foreignTransferTransaction(transaction)
                .build();

        if (request.getIdFile() != null) {
            sender.setIdFilePath(fileUploadService.uploadFileToTransaction(transaction, request.getIdFile(), "ID").getFileUrl());
        }
        if (request.getProofDocumentFile() != null) {
            sender.setProofDocumentFilePath(fileUploadService.uploadFileToTransaction(transaction, request.getProofDocumentFile(), "PROOF").getFileUrl());
        }
        if (request.getRelationDocumentFile() != null) {
            sender.setRelationDocumentFilePath(fileUploadService.uploadFileToTransaction(transaction, request.getRelationDocumentFile(), "RELATION").getFileUrl());
        }

        senderRepository.save(sender);

        return SenderResponse.fromEntity(sender);
    }

}