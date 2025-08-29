package com.project.korex.ForeignTransfer.controller;

import com.project.korex.ForeignTransfer.dto.request.SenderRequest;
import com.project.korex.ForeignTransfer.dto.response.SenderResponse;
import com.project.korex.ForeignTransfer.service.SenderService;
import com.project.korex.common.security.user.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/foreign-transfer/sender")
@RequiredArgsConstructor
public class SenderController {

    private final SenderService senderService;

    @Operation(summary = "송금인 등록 및 파일 업로드",
            description = "토큰 기반 사용자 정보를 이용하여 송금인 정보를 등록하고 필요한 파일을 업로드합니다.")
    @PostMapping("/create")
    public ResponseEntity<SenderResponse> createSender(
            @AuthenticationPrincipal CustomUserPrincipal principal, // 토큰에서 유저 정보 가져오기
            @RequestParam("transferId") Long transferId,
            @RequestParam("name") String name,
            @RequestParam("transferReason") String transferReason,
            @RequestParam(value = "countryNumber", required = false) String countryNumber,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "engAddress", required = false) String engAddress,
            @RequestParam(value = "staffMessage", required = false) String staffMessage,
            @RequestParam(value = "relationRecipient", required = false) String relationRecipient,
            @RequestPart(value = "idFile", required = false) MultipartFile idFile,
            @RequestPart(value = "proofDocumentFile", required = false) MultipartFile proofDocumentFile,
            @RequestPart(value = "relationDocumentFile", required = false) MultipartFile relationDocumentFile
    ) {
        Long userId = principal.getUserId(); // JWT 토큰에서 userId 추출

        // DTO 생성
        SenderRequest request = SenderRequest.builder()
                .name(name)
                .transferReason(transferReason)
                .countryNumber(countryNumber)
                .phoneNumber(phoneNumber)
                .email(email)
                .country(country)
                .engAddress(engAddress)
                .staffMessage(staffMessage)
                .relationRecipient(relationRecipient)
                .idFile(idFile)
                .proofDocumentFile(proofDocumentFile)
                .relationDocumentFile(relationDocumentFile)
                .build();

        // 서비스 호출
        SenderResponse response = senderService.createSenderWithTransaction(request, userId);

        return ResponseEntity.ok(response);
    }
}
