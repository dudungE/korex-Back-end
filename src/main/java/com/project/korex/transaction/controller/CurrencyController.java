package com.project.korex.transaction.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
public class CurrencyController {

//    private final CurrencyService currencyService;
//
//    // 지원 통화 목록 조회
//    @GetMapping("/list")
//    public ResponseEntity<List<CurrencyResponseDto>> getSupportedCurrencies() {
//        List<CurrencyResponseDto> currencies = currencyService.getAllCurrencies();
//        return ResponseEntity.ok(currencies);
//    }
//
//    // 특정 통화 정보 조회
//    @GetMapping("/{currencyCode}")
//    public ResponseEntity<CurrencyResponseDto> getCurrency(@PathVariable String currencyCode) {
//        CurrencyResponseDto currency = currencyService.getCurrency(currencyCode);
//        return ResponseEntity.ok(currency);
//    }
}