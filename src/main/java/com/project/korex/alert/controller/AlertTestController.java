package com.project.korex.alert.controller;

import com.project.korex.alert.service.AlertService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class AlertTestController {

    @Autowired
    private AlertService alertService; // 해당 메서드가 포함된 서비스 클래스

    /**
     * 환율 알람 테스트용 엔드포인트
     * 특정 통화 코드와 환율을 입력받아 알람 발송 테스트
     */
    @PostMapping("/alert/{currencyCode}/{rate}")
    public ResponseEntity<Map<String, Object>> testAlert(
            @PathVariable String currencyCode,
            @PathVariable double rate) {

        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("==============================================================");
            log.info("[testAlert]Testing alert for currency: {}, rate: {}", currencyCode, rate);

            // 실제 알람 체크 및 발송 로직 호출
            alertService.checkAndSendAlerts(currencyCode, rate);

            response.put("success", true);
            response.put("message", "Alert test completed successfully");
            response.put("currencyCode", currencyCode);
            response.put("testRate", rate);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error during alert test for currency: {}, rate: {}", currencyCode, rate, e);

            response.put("success", false);
            response.put("message", "Alert test failed: " + e.getMessage());
            response.put("currencyCode", currencyCode);
            response.put("testRate", rate);
            response.put("error", e.getClass().getSimpleName());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST 요청으로 여러 통화 동시 테스트
     */
    @PostMapping("/alert/bulk")
    public ResponseEntity<Map<String, Object>> testMultipleAlerts(
            @RequestBody List<AlertTestRequest> testRequests) {

        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> results = new ArrayList<>();

        for (AlertTestRequest request : testRequests) {
            Map<String, Object> result = new HashMap<>();
            try {
                log.info("Testing alert for currency: {}, rate: {}",
                        request.getCurrencyCode(), request.getRate());

                alertService.checkAndSendAlerts(request.getCurrencyCode(), request.getRate());

                result.put("currencyCode", request.getCurrencyCode());
                result.put("rate", request.getRate());
                result.put("success", true);
                result.put("message", "Success");

            } catch (Exception e) {
                result.put("currencyCode", request.getCurrencyCode());
                result.put("rate", request.getRate());
                result.put("success", false);
                result.put("message", e.getMessage());
            }
            results.add(result);
        }

        response.put("results", results);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    /**
     * GET 요청으로 간단한 테스트 (쿼리 파라미터 사용)
     */
    @GetMapping("/alert")
    public ResponseEntity<String> testAlertWithParams(
            @RequestParam String currency,
            @RequestParam double rate) {

        try {
            alertService.checkAndSendAlerts(currency, rate);
            return ResponseEntity.ok(
                    String.format("Alert test completed for %s at rate %.4f", currency, rate)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Test failed: " + e.getMessage());
        }
    }

    // 벌크 테스트용 요청 DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertTestRequest {
        private String currencyCode;
        private double rate;
    }
}
