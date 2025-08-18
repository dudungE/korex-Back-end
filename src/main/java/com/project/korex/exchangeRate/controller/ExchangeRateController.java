package com.project.korex.exchangeRate.controller;

import com.project.korex.exchangeRate.dto.ExchangeRateDto;
import com.project.korex.exchangeRate.service.ExchangeRateCrawlerService;
import com.project.korex.exchangeRate.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateCrawlerService exchangeRateCrawlerService;

    /**
     * 실시간 환율 데이터 조회
     */
    @GetMapping("/real-time")
    @Operation(summary = "실시간 환율 데이터 조회(네이버 환율 크롤링)")
    public ResponseEntity<List<Map<String, String>>> getExchangeRates() {

        // 캐시에 값이 있으면 우선 반환
        List<Map<String, String>> cached = exchangeRateCrawlerService.getRealtimeRateFromCache();
        if (cached != null && !cached.isEmpty()) {
            System.out.println("jjhdebug: from cache");
            return ResponseEntity.ok(cached);
        }
        // 없으면 크롤링 후 캐시에 저장
        try {
            List<Map<String, String>> exchangeRates = exchangeRateCrawlerService.crawlRealtimeRate();
            System.out.println("jjhdebug: from crawling");
            return ResponseEntity.ok(exchangeRates);
        } catch (IOException e) {
            // log.error("환율 데이터 크롤링 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 특정 날짜에 여러 통화코드 환율 조회
     */
    @GetMapping("/by-date")
    @Operation(summary = "특정 날짜에 여러 통화코드 환율 조회(PostgreSQL DataBase)")
    public List<ExchangeRateDto> getRatesByDateAndCurrencies(
//            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
            @RequestParam("date") String dateStr) {
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception e) {
            date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
        }

        // 출력 통화 지정
        List<String> currencies = Arrays.asList("USD", "EUR", "JPY", "CNY", "CAD", "CHF");

        return exchangeRateService.getExchangeRatesByDateAndCurrencies(date, currencies);
    }

    /**
     * 특정 통화코드에 대한 일자별 환율 조회
     */
    @GetMapping("/by-currency/{currencyCode}")
    @Operation(summary = "특정 통화코드에 대한 일자별 환율 조회(PostgreSQL DataBase)")
    public List<ExchangeRateDto> getRatesByCurrencyOrderedByDate(
            @PathVariable String currencyCode) {

        return exchangeRateService.getExchangeRatesByCurrencyOrderedByDate(currencyCode);
    }

}
