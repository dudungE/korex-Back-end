package com.project.korex.ForeignTransfer.controller;

import com.project.korex.ForeignTransfer.dto.request.ForeignTransferRequest;
import com.project.korex.ForeignTransfer.dto.response.ForeignTransferResponse;
import com.project.korex.ForeignTransfer.service.ForeignTransferService;
import com.project.korex.common.security.user.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/foreign-transfer")
@RequiredArgsConstructor
public class ForeignTransferController {

    private final ForeignTransferService foreignTransferService;

    @PostMapping("/request")
    public ResponseEntity<ForeignTransferResponse> createForeignTransfer(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestPart("request") ForeignTransferRequest request, // JSON 데이터
            @RequestPart(value = "idFile", required = false) MultipartFile idFile,
            @RequestPart(value = "proofDocumentFile", required = false) MultipartFile proofDocumentFile,
            @RequestPart(value = "relationDocumentFile", required = false) MultipartFile relationDocumentFile
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        // request 안에 일반 데이터 + 파일이 따로 있으므로, 서비스에서 파일 처리 가능
        ForeignTransferResponse response = foreignTransferService.processFullForeignTransfer(
                principal.getName(), request, idFile, proofDocumentFile, relationDocumentFile
        );

        return ResponseEntity.ok(response);
    }

}
