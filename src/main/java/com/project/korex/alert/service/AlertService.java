package com.project.korex.alert.service;

import com.project.korex.alert.domain.AlertCondition;
import com.project.korex.alert.entity.AlertHistory;
import com.project.korex.alert.entity.AlertSetting;
import com.project.korex.alert.repository.AlertHistoryRepository;
import com.project.korex.alert.repository.AlertSettingRepository;
import com.project.korex.user.entity.Users;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AlertService {

    private final AlertSettingRepository alertSettingRepository;
    private final AlertHistoryRepository alertHistoryRepository;
    private final UserJpaRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final EmailAlertService emailAlertService;

    private static final String ALERT_SENT_KEY = "alert_sent:";
    private static final String REDIS_EXCHANGE_KEY = "exchange:realtime";

    /**
     * 특정 통화에 대한 알람 확인 및 발송 (크롤러에서 호출)
     */
    public void checkAndSendAlerts(String currencyCode, double currentRate) {
        try {
            BigDecimal currentRateBD = BigDecimal.valueOf(currentRate);

            log.info("Checking alerts for currency: {}, current rate: {}", currencyCode, currentRate);

            List<AlertSetting> triggeredAlerts = alertSettingRepository
                    .findTriggeredAlerts(currencyCode, currentRateBD);

            if (triggeredAlerts.isEmpty()) {
                log.info("No triggered alerts found for currency: {}", currencyCode);
                return;
            }

            log.info("[checkAndSendAlerts]Processing {} triggered alerts for currency: {}, current rate: {}",
                    triggeredAlerts.size(), currencyCode, currentRate);

            int processedCount = 0;
            for (AlertSetting alert : triggeredAlerts) {
                if (processAlert(alert, currentRateBD)) {
                    processedCount++;
                }
            }

            log.info("Alert processing completed for currency: {} - Processed: {}/{}",
                    currencyCode, processedCount, triggeredAlerts.size());

        } catch (Exception e) {
            log.error("Error occurred while checking alerts for currency: {}", currencyCode, e);
        }
    }

    /**
     * 개별 알람 처리
     */
    private boolean processAlert(AlertSetting alert, BigDecimal currentRate) {
        try {
            log.info("[processAlert] - Alert ID: {}, User ID: {}, Current rate: {}, Target rate: {}",
                    alert.getId(), alert.getUser().getId(), currentRate, alert.getTargetRate());

            // 중복 발송 방지 확인 (1시간 내)
            String alertKey = generateAlertKey(alert.getId());

            if (Boolean.TRUE.equals(redisTemplate.hasKey(alertKey))) {
                log.info("[processAlert]Skipping alert due to duplicate prevention - Alert ID: {}", alert.getId());
                return false;
            }

            // 알람 발송
            boolean success = emailAlertService.sendAlert(alert, currentRate);

            // 히스토리 저장
            AlertHistory history = success ?
                    new AlertHistory(alert, currentRate) :
                    new AlertHistory(alert, currentRate, "Email sending failed");

            alertHistoryRepository.save(history);

            if (success) {
                // 중복 발송 방지를 위한 Redis 키 설정 (1시간)
                redisTemplate.opsForValue().set(alertKey, "sent", 1, TimeUnit.HOURS);

                log.info("Alert sent successfully - Alert ID: {}, User: {}, Currency: {}, Rate: {}",
                        alert.getId(), alert.getUserEmail(), alert.getCurrencyCode(), currentRate);
            } else {
                log.warn("Alert sending failed - Alert ID: {}", alert.getId());
            }

            return success;

        } catch (Exception e) {
            log.error("Error processing alert - Alert ID: {}", alert.getId(), e);

            // 실패 히스토리 저장
            AlertHistory failureHistory = new AlertHistory(alert, currentRate,
                    "Alert processing error: " + e.getMessage());
            alertHistoryRepository.save(failureHistory);

            return false;
        }
    }

    /**
     * 모든 활성 알람을 수동으로 확인
     */
    @SuppressWarnings("unchecked")
    public void checkAllActiveAlerts() {
        try {
            log.info("Starting manual check for all active alerts");

            List<Map<String, String>> exchangeList = (List<Map<String, String>>)
                    redisTemplate.opsForValue().get(REDIS_EXCHANGE_KEY);

            if (exchangeList == null || exchangeList.isEmpty()) {
                log.warn("No exchange rate data found in Redis");
                return;
            }

            int processedCurrencies = 0;
            for (Map<String, String> rateData : exchangeList) {
                String currencyCode = rateData.get("currency_code");
                String baseRateStr = rateData.get("base_rate");

                if (currencyCode != null && baseRateStr != null && !baseRateStr.isEmpty()) {
                    try {
                        double currentRate = Double.parseDouble(baseRateStr.replaceAll(",", ""));
                        checkAndSendAlerts(currencyCode, currentRate);
                        processedCurrencies++;
                    } catch (NumberFormatException e) {
                        log.error("Failed to parse exchange rate - Currency: {}, Value: {}",
                                currencyCode, baseRateStr);
                    }
                }
            }

            log.info("Manual alert check completed - Processed currencies: {}", processedCurrencies);

        } catch (Exception e) {
            log.error("Error occurred during manual alert check", e);
        }
    }

    /**
     * 새로운 알람 생성
     */
    public AlertSetting createAlert(Long userId, String currencyCode,
                                    BigDecimal targetRate,
                                    AlertCondition condition) {

        log.debug("Creating alert - User ID: {}, Currency: {}, Target rate: {}, Condition: {}",
                userId, currencyCode, targetRate, condition);

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found - User ID: {}", userId);
                    return new IllegalArgumentException("User not found: " + userId);
                });

        // 입력값 검증
        validateAlertInput(currencyCode, targetRate, condition);

        // 중복 알람 확인
        Optional<AlertSetting> duplicateAlert = alertSettingRepository
                .findDuplicateAlert(userId, currencyCode.toUpperCase(), targetRate, condition);

        if (duplicateAlert.isPresent()) {
            log.warn("Duplicate alert detected - User ID: {}, Currency: {}, Target: {}, Condition: {}",
                    userId, currencyCode, targetRate, condition);
            throw new IllegalArgumentException("An alert with the same conditions already exists");
        }

        AlertSetting alert = AlertSetting.builder()
                .user(user)
                .currencyCode(currencyCode.toUpperCase())
                .targetRate(targetRate)
                .condition(condition)
                .build();

        AlertSetting savedAlert = alertSettingRepository.save(alert);

        log.info("Alert created successfully - Alert ID: {}, User ID: {}, Currency: {}",
                savedAlert.getId(), userId, currencyCode);

        return savedAlert;
    }

    /**
     * 사용자의 활성 알람 조회
     */
    @Transactional(readOnly = true)
    public List<AlertSetting> getUserAlerts(Long userId) {
        log.debug("Fetching active alerts for user ID: {}", userId);
        return alertSettingRepository.findByUserIdAndIsActiveTrue(userId);
    }

    /**
     * 사용자의 모든 알람 조회 (비활성 포함)
     */
    @Transactional(readOnly = true)
    public List<AlertSetting> getAllUserAlerts(Long userId) {
        log.debug("Fetching all alerts for user ID: {}", userId);
        return alertSettingRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 권한 확인을 포함한 알람 조회
     */
    @Transactional(readOnly = true)
    public AlertSetting getAlertById(Long alertId, Long userId) {
        log.debug("Fetching alert - Alert ID: {}, User ID: {}", alertId, userId);

        AlertSetting alert = alertSettingRepository.findById(alertId)
                .orElseThrow(() -> {
                    log.error("Alert not found - Alert ID: {}", alertId);
                    return new IllegalArgumentException("Alert not found: " + alertId);
                });

        if (!alert.getUser().getId().equals(userId)) {
            log.warn("Unauthorized alert access attempt - Alert ID: {}, User ID: {}", alertId, userId);
            throw new IllegalArgumentException("You don't have permission to access this alert");
        }

        return alert;
    }

    /**
     * 알람 활성화/비활성화 토글
     */
    public void toggleAlert(Long alertId, Long userId, boolean isActive) {
        log.debug("Toggling alert - Alert ID: {}, User ID: {}, New status: {}",
                alertId, userId, isActive);

        AlertSetting alert = getAlertById(alertId, userId);

        if (isActive) {
            alert.activate();
        } else {
            alert.deactivate();
        }

        alertSettingRepository.save(alert);

        log.info("Alert status changed - Alert ID: {}, User ID: {}, Active: {}",
                alertId, userId, isActive);
    }

    /**
     * 알람 수정
     */
    public AlertSetting updateAlert(Long alertId, Long userId, BigDecimal targetRate, AlertCondition condition) {
        log.debug("Updating alert - Alert ID: {}, User ID: {}, New target: {}, New condition: {}",
                alertId, userId, targetRate, condition);

        AlertSetting alert = getAlertById(alertId, userId);

        validateAlertInput(alert.getCurrencyCode(), targetRate, condition);

        if (alert.getTargetRate().equals(targetRate) && alert.getCondition().equals(condition)) {
            log.debug("No changes detected in alert update - Alert ID: {}", alertId);
            return alert;
        }

        alert.setTargetRate(targetRate);
        alert.setCondition(condition);

        AlertSetting updatedAlert = alertSettingRepository.save(alert);

        log.info("Alert updated successfully - Alert ID: {}", alertId);

        return updatedAlert;
    }

    /**
     * 알람 삭제
     */
    public void deleteAlert(Long alertId, Long userId) {
        log.debug("Deleting alert - Alert ID: {}, User ID: {}", alertId, userId);

        AlertSetting alert = getAlertById(alertId, userId);
        alertSettingRepository.delete(alert);

        log.info("Alert deleted successfully - Alert ID: {}, User ID: {}", alertId, userId);
    }

    /**
     * 사용자의 알람 히스토리 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<AlertHistory> getUserAlertHistory(Long userId, Pageable pageable) {
        log.debug("Fetching alert history for user ID: {}", userId);
        return alertHistoryRepository.findByUserIdOrderBySentAtDesc(userId, pageable);
    }

    /**
     * Redis에서 현재 환율 조회
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public BigDecimal getCurrentRateFromRedis(String currencyCode) {
        try {
            log.debug("Fetching current rate from Redis for currency: {}", currencyCode);

            List<Map<String, String>> exchangeList = (List<Map<String, String>>)
                    redisTemplate.opsForValue().get(REDIS_EXCHANGE_KEY);

            if (exchangeList != null) {
                for (Map<String, String> rateData : exchangeList) {
                    if (currencyCode.toUpperCase().equals(rateData.get("currency_code"))) {
                        String baseRateStr = rateData.get("base_rate");
                        if (baseRateStr != null && !baseRateStr.isEmpty()) {
                            BigDecimal rate = new BigDecimal(baseRateStr.replaceAll(",", ""));
                            log.debug("Current rate found for {}: {}", currencyCode, rate);
                            return rate;
                        }
                    }
                }
            }

            log.debug("No current rate found for currency: {}", currencyCode);
            return null;

        } catch (Exception e) {
            log.error("Error fetching current rate from Redis for currency: {}", currencyCode, e);
            return null;
        }
    }

    // === Private Helper Methods ===

    /**
     * 알람 키 생성
     */
    private String generateAlertKey(Long alertId) {
        return ALERT_SENT_KEY + alertId + ":" +
                LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * 알람 입력값 검증
     */
    private void validateAlertInput(String currencyCode, BigDecimal targetRate, AlertCondition condition) {
        log.debug("Validating alert input - Currency: {}, Target rate: {}, Condition: {}",
                currencyCode, targetRate, condition);

        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code is required");
        }

        if (targetRate == null || targetRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Target rate must be greater than zero");
        }

        if (condition == null) {
            throw new IllegalArgumentException("Alert condition is required");
        }

        if (targetRate.compareTo(new BigDecimal("10000000")) > 0) {
            throw new IllegalArgumentException("Target rate is too high (maximum: 10,000,000)");
        }

        log.debug("Alert input validation passed");
    }
}
