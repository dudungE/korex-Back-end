package com.project.korex.ForeignTransfer.service;

import com.project.korex.ForeignTransfer.dto.response.ForeignTransferHistoryResponse;
import com.project.korex.ForeignTransfer.entity.ForeignTransferTransaction;
import com.project.korex.ForeignTransfer.repository.ForeignTransferTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ForeignTransferHistoryService {

    private final ForeignTransferTransactionRepository transactionRepository;

    public List<ForeignTransferHistoryResponse> getUserTransferHistory(String loginId) {
        List<ForeignTransferTransaction> transactions =
                transactionRepository.findAllByUser_LoginId(loginId);

        return transactions.stream().map(tx -> ForeignTransferHistoryResponse.builder()
                        .transferId(tx.getId())
                        .transferAmount(tx.getTransferAmount())
                        .convertedAmount(tx.getConvertedAmount())
                        .appliedRate(tx.getExchangeRate())
                        .feeAmount(tx.getTransaction().getFeeAmount())
                        .totalDeductedAmount(tx.getTransaction().getTotalDeductedAmount())
                        .transferStatus(tx.getTransferStatus().name())
                        .requestStatus(tx.getRequestStatus().name())
                        .transferReason(tx.getStaffMessage())
                        .senderName(tx.getSender().getName())
                        .createdAt(tx.getCreatedAt())
                        .agreedAt(tx.getTermsAgreement() != null ? tx.getTermsAgreement().getAgreedAt() : null)
                        .build())
                .toList();
    }
}
