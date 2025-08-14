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
    // @GetMapping 메서드에 ResponseEntity를 붙이면 HTTP 상태 코드, 헤더 등을 더 명확히 제어
    @GetMapping("/real-time")
    @Operation(summary = "실시간 환율 데이터 조회(네이버 환율 크롤링)")
    public ResponseEntity<List<Map<String, String>>> getExchangeRates() {
        try {
            List<Map<String, String>> exchangeRates = exchangeRateCrawlerService.crawlRealtimeRate();
            return ResponseEntity.ok(exchangeRates);  // HTTP 200 OK
        } catch (IOException e) {
            // 에러 로그
            // log.error("환율 데이터 크롤링 실패", e);
            return ResponseEntity.internalServerError().build();  // HTTP 500
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
