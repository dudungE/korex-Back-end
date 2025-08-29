package com.project.korex.ForeignTransfer.controller;

import com.project.korex.ForeignTransfer.dto.request.TermsAgreeRequest;
import com.project.korex.ForeignTransfer.dto.response.TermsAgreementResponse;
import com.project.korex.ForeignTransfer.service.TermsAgreementService;
import com.project.korex.common.security.user.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/foreign-transfer/terms")
@RequiredArgsConstructor
public class TermsAgreementController {

    private final TermsAgreementService termsAgreementService;

    @PostMapping
    public ResponseEntity<TermsAgreementResponse> agreeTerms(
            @Parameter(description = "약관 동의 체크값", required = true)
            @RequestBody TermsAgreeRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long userId = principal.getUserId();
        TermsAgreementResponse response = termsAgreementService.agreeTermsAndCreateTransaction(request, userId);
        return ResponseEntity.ok(response);
    }
}
