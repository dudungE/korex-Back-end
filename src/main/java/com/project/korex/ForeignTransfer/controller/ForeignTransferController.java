package com.project.korex.ForeignTransfer.controller;

import com.project.korex.ForeignTransfer.dto.request.SenderTransferRequest;
import com.project.korex.ForeignTransfer.dto.response.SenderTransferResponse;
import com.project.korex.ForeignTransfer.service.ForeignTransferService;
import com.project.korex.common.security.user.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/foreign-transfer")
@RequiredArgsConstructor
public class ForeignTransferController {

    private final ForeignTransferService foreignTransferService;

    @PostMapping("/request")
    public ResponseEntity<SenderTransferResponse> requestTransfer(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody SenderTransferRequest request) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String loginId = principal.getName(); // JWT에서 추출된 사용자 정보
        SenderTransferResponse response = foreignTransferService.createForeignTransfer(loginId, request);
        return ResponseEntity.ok(response);
    }

}
