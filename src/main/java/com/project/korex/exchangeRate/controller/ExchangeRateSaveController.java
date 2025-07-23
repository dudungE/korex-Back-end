package com.project.korex.exchangeRate.controller;

import com.project.korex.exchangeRate.entity.ExchangeRate;
import com.project.korex.exchangeRate.service.ExchangeRateSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/exchange/save")
@RequiredArgsConstructor
public class ExchangeRateSaveController {

    private final ExchangeRateSaveService exchangeRateSaveService;

    /**
     * 환율 데이터 저장 (리스트와 기준 날짜를 함께 전송)
     */
    @PostMapping("/rates")
    public ResponseEntity<String> saveExchangeRates(
//            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("date") @DateTimeFormat(pattern = "yyyyMMdd") String date) {
        exchangeRateSaveService.saveExchangeRatesByDate(date);
        return ResponseEntity.ok("환율 데이터가 저장되었습니다.");
    }

    /**
     * 날짜별 환율 데이터 조회
     */
    @GetMapping("/rates")
    public ResponseEntity<List<ExchangeRate>> getExchangeRates(
//            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("date") @DateTimeFormat(pattern = "yyyyMMdd") LocalDate date) {
        List<ExchangeRate> rates = exchangeRateSaveService.findRatesByDate(date);
        if (rates.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rates);
    }
}
