package com.project.korex.externalAccount.controller;

import com.project.korex.common.security.user.CustomUserPrincipal;
import com.project.korex.externalAccount.dto.request.AddExternalAccountRequestDto;
import com.project.korex.externalAccount.dto.response.ExternalAccountResponseDto;
import com.project.korex.externalAccount.service.ExternalAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/external-accounts")
@RequiredArgsConstructor
@Validated
public class ExternalAccountController {

    private final ExternalAccountService externalAccountService;

    @GetMapping
    public ResponseEntity<List<ExternalAccountResponseDto>> getExternalAccounts(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {

        List<ExternalAccountResponseDto> accounts = externalAccountService.getUserAccounts(userPrincipal.getUserId());
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ExternalAccountResponseDto> addExternalAccount(
            @RequestBody @Valid AddExternalAccountRequestDto request,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {

        ExternalAccountResponseDto response = externalAccountService.addAccount(userPrincipal.getUserId(), request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{externalAccountId}/set-primary")
    public ResponseEntity<ExternalAccountResponseDto> setPrimaryAccount(
            @PathVariable Long externalAccountId,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {

        ExternalAccountResponseDto response = externalAccountService.setPrimaryAccount(
                userPrincipal.getUserId(), externalAccountId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{externalAccountId}")
    public ResponseEntity<Void> deleteExternalAccount(
            @PathVariable Long externalAccountId,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {

        externalAccountService.deleteAccount(userPrincipal.getUserId(), externalAccountId);
        return ResponseEntity.noContent().build();
    }
}

