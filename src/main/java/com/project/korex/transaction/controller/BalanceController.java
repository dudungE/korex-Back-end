package com.project.korex.transaction.controller;

import com.project.korex.common.security.user.CustomUserPrincipal;
import com.project.korex.common.util.CookieUtil;
import com.project.korex.transaction.dto.response.BalanceResponseDto;
import com.project.korex.transaction.service.BalanceService;
import com.project.korex.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/balance")
@RequiredArgsConstructor
@Slf4j
//@CrossOrigin(origins = "*")
public class BalanceController {

    private final BalanceService balanceService;
    private final CookieUtil cookieUtil;

    @GetMapping("/{userId}")
    public ResponseEntity<List<BalanceResponseDto>> getUserBalances(@PathVariable Long userId) {
        List<BalanceResponseDto> balances = balanceService.getUserBalances(userId);
        return new ResponseEntity<>(balances, HttpStatus.OK);
    }

    /**
     * 환전용 - 현재 사용자의 모든 잔액 조회
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getBalancesForExchange(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        try {
            if (userPrincipal == null || userPrincipal.getUser() == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "인증되지 않은 사용자입니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            Long userId = userPrincipal.getUser().getId();
            Map<String, BalanceResponseDto> balances = balanceService.getUserBalancesForExchange(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "잔액 조회 성공");
            response.put("balances", balances);
            response.put("timestamp", System.currentTimeMillis());

            log.info("환전용 잔액 조회 성공 - userId: {}", userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("환전용 잔액 조회 실패: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "잔액 조회 중 오류가 발생했습니다.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
