package com.project.korex.ForeignTransfer.controller;

import com.project.korex.ForeignTransfer.dto.request.RecipientRequest;
import com.project.korex.ForeignTransfer.dto.response.RecipientResponse;
import com.project.korex.ForeignTransfer.service.ForeignTransferRecipientService;
import com.project.korex.common.security.user.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ForeignTransfer/recipients")
@RequiredArgsConstructor
public class ForeignTransferRecipientController {

    private final ForeignTransferRecipientService recipientService;

    @PostMapping
    public ResponseEntity<RecipientResponse> createRecipient(
            @RequestBody RecipientRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long userId = principal.getUserId();
        RecipientResponse response = recipientService.createRecipient(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 단건 조회
    @GetMapping("/{recipientId}")
    public ResponseEntity<RecipientResponse> getRecipient(
            @PathVariable Long recipientId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long userId = principal.getUserId();
        RecipientResponse response = recipientService.getRecipientById(userId, recipientId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RecipientResponse>> getRecipientsByUserId(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long userId = principal.getUserId();
        List<RecipientResponse> recipients = recipientService.getRecipientsByUserId(userId);
        return ResponseEntity.ok(recipients);
    }

    @PutMapping("/{recipientId}")
    public ResponseEntity<RecipientResponse> updateRecipient(
            @PathVariable Long recipientId,
            @RequestBody RecipientRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long userId = principal.getUserId();
        RecipientResponse response = recipientService.updateRecipient(userId, recipientId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{recipientId}")
    public ResponseEntity<Void> deleteRecipient(
            @PathVariable Long recipientId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long userId = principal.getUserId();
        recipientService.deleteRecipient(userId, recipientId);
        return ResponseEntity.noContent().build();
    }
}
