package com.project.korex.ForeignTransfer.service;

import com.project.korex.ForeignTransfer.dto.request.SenderRequest;
import com.project.korex.ForeignTransfer.entity.ForeignTransferTransaction;
import com.project.korex.ForeignTransfer.entity.Sender;
import com.project.korex.ForeignTransfer.enums.RequestStatus;
import com.project.korex.ForeignTransfer.enums.TransferStatus;
import com.project.korex.ForeignTransfer.repository.ForeignTransferTransactionRepository;
import com.project.korex.ForeignTransfer.repository.SenderRepository;
import com.project.korex.ForeignTransfer.service.FileUploadService;
import com.project.korex.user.entity.Users;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SenderService {

    private final UserJpaRepository userRepository;
    private final SenderRepository senderRepository;
    private final ForeignTransferTransactionRepository transactionRepository;
    private final FileUploadService fileUploadService;

    @Transactional
    public Sender createSenderWithTransaction(String loginId, SenderRequest request) {
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 1️⃣ 송금 트랜잭션 생성
        ForeignTransferTransaction transaction = ForeignTransferTransaction.builder()
                .user(user)
                .transferStatus(TransferStatus.NOT_STARTED)
                .requestStatus(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);

        // 2️⃣ Sender 생성 및 파일 업로드
        Sender sender = Sender.builder()
                .user(user)
                .foreignTransferTransaction(transaction)
                .name(request.getName())
                .transferReason(request.getTransferReason())
                .countryNumber(request.getCountryNumber())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .country(request.getCountry())
                .engAddress(request.getEngAddress())
                .staffMessage(request.getStaffMessage())
                .relationRecipient(request.getRelationRecipient())
                .idFilePath(saveFilePath(transaction, request.getIdFile(), "ID"))
                .proofDocumentFilePath(saveFilePath(transaction, request.getProofDocumentFile(), "PROOF"))
                .relationDocumentFilePath(saveFilePath(transaction, request.getRelationDocumentFile(), "RELATION"))
                .build();

        transaction.setSender(sender);

        return senderRepository.save(sender);
    }

    // 파일 저장 후 경로 반환
    private String saveFilePath(ForeignTransferTransaction transaction,
                                org.springframework.web.multipart.MultipartFile file,
                                String fileType) {
        if (file == null || file.isEmpty()) return null;
        return fileUploadService.uploadFileToTransaction(transaction, file, fileType).getFileUrl();
    }
}
