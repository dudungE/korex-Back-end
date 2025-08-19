package com.project.korex.transaction.controller;

import com.project.korex.transaction.dto.response.BalanceResponseDto;
import com.project.korex.transaction.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/balance")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<BalanceResponseDto>> getUserBalances(@PathVariable Long userId) {
        List<BalanceResponseDto> balances = balanceService.getUserBalances(userId);
        return new ResponseEntity<>(balances, HttpStatus.OK);
    }
}
