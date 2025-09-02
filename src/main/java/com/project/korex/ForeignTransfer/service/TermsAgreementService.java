// TermsAgreementService.java
package com.project.korex.ForeignTransfer.service;

import com.project.korex.ForeignTransfer.entity.ForeignTransferTransaction;
import com.project.korex.ForeignTransfer.entity.TermsAgreement;
import com.project.korex.ForeignTransfer.repository.ForeignTransferTransactionRepository;
import com.project.korex.ForeignTransfer.repository.TermsAgreementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TermsAgreementService {

    private final TermsAgreementRepository agreementRepository;
    private final ForeignTransferTransactionRepository transactionRepository;

    @Transactional
    public TermsAgreement agreeTerms(Long transactionId, boolean agree1, boolean agree2, boolean agree3) {
        ForeignTransferTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("트랜잭션이 존재하지 않습니다."));

        TermsAgreement agreement = new TermsAgreement();
        agreement.setForeignTransferTransaction(transaction);
        agreement.setAgree1(agree1);
        agreement.setAgree2(agree2);
        agreement.setAgree3(agree3);
        agreement.setAgreed(agree1 && agree2);
        agreement.setAgreedAt(LocalDateTime.now());

        transaction.setTermsAgreement(agreement);
        return agreementRepository.save(agreement);
    }
}
