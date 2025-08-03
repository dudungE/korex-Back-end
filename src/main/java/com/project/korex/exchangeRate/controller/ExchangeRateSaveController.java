package com.project.korex.exchangeRate.controller;

import com.project.korex.exchangeRate.service.ExchangeRateCrawlerService;
import com.project.korex.exchangeRate.service.ExchangeRateSaveService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exchange/save")
@RequiredArgsConstructor
public class ExchangeRateSaveController {


    private final ExchangeRateCrawlerService exchangeRateCrawlerService;
    private final ExchangeRateSaveService currencyRateSaveService;

    /**
     * 환율 데이터 저장 (리스트와 기준 날짜를 함께 전송)
     */

    @PostMapping("/crawl/rates")
    @Operation(summary = "[TEST]개별 통화 일자별 환율 데이터 저장(네이버 환율 크롤링 - page별 10개 data)")
    public void saveDailyRate(
            @RequestParam(value = "currencyCode", defaultValue = "USD") String currencyCode,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {

        try {
            currencyRateSaveService.saveCurrencyRateDaily(currencyCode, page);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("/crawl/rates")
    @Operation(summary = "[TEST]개별 통화 일자별 환율 데이터 조회(네이버 환율 크롤링 - page별 10개 data)")
    public ResponseEntity<List<Map<String, String>>> getDailyRate(
            @RequestParam(value = "currencyCode", defaultValue = "USD") String currencyCode,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        try {
            List<Map<String, String>> rateList = exchangeRateCrawlerService.crawlDailyRate(currencyCode, page);
            return ResponseEntity.ok(rateList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
