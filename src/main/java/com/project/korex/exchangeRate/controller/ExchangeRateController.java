package com.project.korex.exchangeRate.controller;

import com.project.korex.exchangeRate.dto.ExchangeRateDto;
import com.project.korex.exchangeRate.service.ExchangeRateCrawlerService;
import com.project.korex.exchangeRate.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateCrawlerService exchangeRateCrawlerService;

    @GetMapping("/rates")
    @Operation(summary = "일자별 고시된 환율 조회(수출입은행 api)")
    public ResponseEntity<List<ExchangeRateDto>> getExchangeRates(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().format(T(java.time.format.DateTimeFormatter).ofPattern('yyyyMMdd'))}") String searchdate
    ) {
        return ResponseEntity.ok(exchangeRateService.getExchangeDataAsDtoList(searchdate));
    }

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

}
