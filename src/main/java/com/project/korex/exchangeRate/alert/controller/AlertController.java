package com.project.korex.exchangeRate.alert.controller;

import com.project.korex.exchangeRate.alert.domain.AlertCondition;
import com.project.korex.exchangeRate.alert.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_KEY = "exchange:realtime";

    /**
     * 알림 등록
     */
    @PostMapping("/create")
    public ResponseEntity<String> createAlert(@RequestParam String userId,
                                              @RequestParam String currency,
                                              @RequestParam double targetRate,
                                              @RequestParam AlertCondition condition) {
        String alertId = alertService.createAlert(userId, currency, targetRate, condition);
        return ResponseEntity.ok("알림 등록 완료 (ID: " + alertId + ")");
    }

    /**
     * 알림 삭제
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAlert(@RequestParam String userId,
                                              @RequestParam String currency) {
        alertService.deleteAlert(userId, currency);
        return ResponseEntity.ok("알림 삭제 완료");
    }

    /**
     * 모든 알림 조회
     */
    @GetMapping("/list")
    public ResponseEntity<String> listAlerts() {
        alertService.showAllAlerts();
        return ResponseEntity.ok("콘솔에서 알림 목록 확인");
    }

    /**
     * 실시간 환율 데이터 조회
     */
    @GetMapping("/realtime-rates")
    @SuppressWarnings("unchecked")
    public ResponseEntity<List<Map<String, String>>> getRealtimeRates() {
        List<Map<String, String>> rates = (List<Map<String, String>>) redisTemplate.opsForValue().get(REDIS_KEY);
        return ResponseEntity.ok(rates);
    }

    /**
     * 수동 알림 테스트
     */
    @PostMapping("/test")
    public ResponseEntity<String> testAlert(@RequestParam String currency,
                                            @RequestParam double currentRate) {
        alertService.checkAndSendAlerts(currency, currentRate);
        return ResponseEntity.ok("알림 테스트 완료: " + currency + " = " + currentRate);
    }
}
