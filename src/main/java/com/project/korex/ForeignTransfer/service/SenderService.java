// SenderService.java
package com.project.korex.ForeignTransfer.service;

import com.project.korex.ForeignTransfer.dto.request.SenderRequest;
import com.project.korex.ForeignTransfer.entity.ForeignTransferTransaction;
import com.project.korex.ForeignTransfer.entity.Sender;
import com.project.korex.ForeignTransfer.repository.ForeignTransferTransactionRepository;
import com.project.korex.ForeignTransfer.repository.SenderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SenderService {

    private final SenderRepository senderRepository;
    private final FileUploadService fileUploadService;
    private final ForeignTransferTransactionRepository transactionRepository;

    @Transactional
    public Sender createSender(Long transactionId, SenderRequest request) {
        ForeignTransferTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("트랜잭션이 존재하지 않습니다."));

        Sender sender = Sender.builder()
                .name(request.getName())
                .transferReason(request.getTransferReason())
                .country(request.getCountry())
                .countryNumber(request.getCountryNumber())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .engAddress(request.getEngAddress())
                .relationRecipient(request.getRelationRecipient())
                .staffMessage(request.getStaffMessage())
                .foreignTransferTransaction(transaction)
                .build();

        java.util.Optional.ofNullable(request.getIdFile())
                .ifPresent(f -> sender.setIdFilePath(fileUploadService.uploadFileToTransaction(transaction, f, "ID").getFileUrl()));
        java.util.Optional.ofNullable(request.getProofDocumentFile())
                .ifPresent(f -> sender.setProofDocumentFilePath(fileUploadService.uploadFileToTransaction(transaction, f, "PROOF").getFileUrl()));
        java.util.Optional.ofNullable(request.getRelationDocumentFile())
                .ifPresent(f -> sender.setRelationDocumentFilePath(fileUploadService.uploadFileToTransaction(transaction, f, "RELATION").getFileUrl()));

        return senderRepository.save(sender);
    }
}
