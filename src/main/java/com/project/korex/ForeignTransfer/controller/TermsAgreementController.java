package com.project.korex.ForeignTransfer.controller;

import com.project.korex.ForeignTransfer.dto.request.TermsAgreeRequest;
import com.project.korex.ForeignTransfer.dto.response.TermsAgreementResponse;
import com.project.korex.ForeignTransfer.entity.ForeignTransferTransaction;
import com.project.korex.ForeignTransfer.entity.TermsAgreement;
import com.project.korex.ForeignTransfer.service.ForeignTransferTransactionFactory;
import com.project.korex.ForeignTransfer.service.TermsAgreementService;
import com.project.korex.common.security.user.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/foreign-transfer/terms")
@RequiredArgsConstructor
public class TermsAgreementController {

    private final TermsAgreementService termsAgreementService;
    private final ForeignTransferTransactionFactory transactionFactory;

    @PostMapping
    public ResponseEntity<TermsAgreementResponse> agreeTerms(
            @RequestBody TermsAgreeRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long transactionId = request.getTransferId();
        TermsAgreement agreement;

        // transferId가 없으면 새 트랜잭션 생성
        if (transactionId == null) {
            ForeignTransferTransaction transaction = transactionFactory.createTransactionForUser(
                    principal.getUser(),
                    request.getBankName(),
                    request.getKrwNumber(),
                    request.getForeignNumber(),
                    request.getAccountPassword()
            );
            transactionId = transaction.getId();
        }

        // 약관 동의 처리
        agreement = termsAgreementService.agreeTerms(
                transactionId,
                request.getAgree1(),
                request.getAgree2(),
                request.getAgree3()
        );

        // DTO 변환
        TermsAgreementResponse response = new TermsAgreementResponse();
        response.setId(agreement.getId());
        response.setTransferId(transactionId);
        response.setAgree1(agreement.getAgree1());
        response.setAgree2(agreement.getAgree2());
        response.setAgree3(agreement.getAgree3());
        response.setAgreedAt(agreement.getAgreedAt());

        return ResponseEntity.ok(response);
    }
}
