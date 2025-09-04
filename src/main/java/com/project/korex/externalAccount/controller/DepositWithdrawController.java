package com.project.korex.externalAccount.controller;


import com.project.korex.common.security.user.CustomUserPrincipal;
import com.project.korex.externalAccount.dto.request.DepositRequestDto;
import com.project.korex.externalAccount.dto.request.WithdrawRequestDto;
import com.project.korex.externalAccount.service.DepositWithdrawService;
import com.project.korex.transaction.dto.response.TransactionResponseDto;
import com.project.korex.user.entity.Users;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deposit-withdraw")
@RequiredArgsConstructor
@Validated
public class DepositWithdrawController {

    private final DepositWithdrawService depositWithdrawService;
    private final UserJpaRepository usersRepository;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDto> deposit(
            @RequestBody @Valid DepositRequestDto request,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {

        Users user = getUserEntity(userPrincipal.getUserId());
        TransactionResponseDto response = depositWithdrawService.deposit(user, request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDto> withdraw(
            @RequestBody @Valid WithdrawRequestDto request,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {

        Users user = getUserEntity(userPrincipal.getUserId());
        TransactionResponseDto response = depositWithdrawService.withdraw(user, request);

        return ResponseEntity.ok(response);
    }

    private Users getUserEntity(Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
    }
}

