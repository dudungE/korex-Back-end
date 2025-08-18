package com.project.korex.transaction.controller;

import com.project.korex.transaction.dto.response.BalanceResponseDto;
import com.project.korex.transaction.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    // 내 잔액 조회
    @GetMapping("/my")
    public ResponseEntity<List<BalanceResponseDto>> getMyBalances() {
        List<BalanceResponseDto> balances = balanceService.getMyBalances();
        return ResponseEntity.ok(balances);
    }

    // 특정 통화 잔액 조회
//    @GetMapping("/my/{currencyCode}")
//    public ResponseEntity<BalanceResponseDto> getMyBalance(@PathVariable String currencyCode) {
//        BalanceResponse balance = balanceService.getMyBalance(currencyCode);
//        return ResponseEntity.ok(balance);
//    }
}