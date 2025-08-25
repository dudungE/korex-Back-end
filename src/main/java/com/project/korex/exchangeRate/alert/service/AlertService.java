package com.project.korex.exchangeRate.alert.service;

import com.project.korex.exchangeRate.alert.domain.AlertCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Service
public class AlertService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String ALERT_PREFIX = "alert:";
    private static final String SENT_PREFIX = "sent:";

    /**
     * Register alert
     */
    public String createAlert(String userId, String currency, double targetRate, AlertCondition condition) {
        String alertId = UUID.randomUUID().toString();
        String alertKey = ALERT_PREFIX + userId + ":" + currency;
        String alertData = alertId + ":" + targetRate + ":" + condition.name();

        redisTemplate.opsForValue().set(alertKey, alertData);

        System.out.println("✅ Alert registered: " + userId + " - " + currency + " " + targetRate + " " + condition.getDescription());
        return alertId;
    }

    /**
     * Check alerts for a specific currency and send if conditions are met
     */
    public void checkAndSendAlerts(String currency, double currentRate) {
        // Query all alert keys for the currency
        Set<String> alertKeys = redisTemplate.keys(ALERT_PREFIX + "*:" + currency);

        if (alertKeys == null || alertKeys.isEmpty()) {
            return; // No alerts for this currency
        }

        System.out.println("🔍 " + currency + " alert check started (current rate: " + currentRate + ")");

        for (String alertKey : alertKeys) {
            String alertData = redisTemplate.opsForValue().get(alertKey);
            if (alertData == null) continue;

            processAlert(alertKey, alertData, currency, currentRate);
        }
    }

    /**
     * Process individual alert
     */
    private void processAlert(String alertKey, String alertData, String currency, double currentRate) {
        try {
            String[] parts = alertData.split(":");
            if (parts.length < 3) return;

            String alertId = parts[0];
            double targetRate = Double.parseDouble(parts[1]);
            AlertCondition condition = AlertCondition.valueOf(parts[2]);
            String userId = alertKey.split(":")[1];

            // Check alert condition
            boolean shouldSend = false;
            switch (condition) {
                case ABOVE:
                    shouldSend = currentRate >= targetRate;
                    break;
                case BELOW:
                    shouldSend = currentRate <= targetRate;
                    break;
            }

            if (shouldSend && !isAlreadySentToday(alertId)) {
                sendAlert(userId, currency, currentRate, targetRate, condition);
                markAsSentToday(alertId);
            }

        } catch (Exception e) {
            System.err.println("❌ Alert processing error: " + e.getMessage());
        }
    }

    /**
     * Check if already sent today
     */
    private boolean isAlreadySentToday(String alertId) {
        String sentKey = SENT_PREFIX + alertId + ":" + LocalDate.now();
        return Boolean.TRUE.equals(redisTemplate.hasKey(sentKey));
    }

    /**
     * Mark as sent today
     */
    private void markAsSentToday(String alertId) {
        String sentKey = SENT_PREFIX + alertId + ":" + LocalDate.now();
        redisTemplate.opsForValue().set(sentKey, "1", java.time.Duration.ofDays(1));
    }

    /**
     * Send alert
     */
    private void sendAlert(String userId, String currency, double currentRate, double targetRate, AlertCondition condition) {
        String message = String.format("🚨 %s Exchange Rate Alert!\nTarget: %.2f %s\nCurrent: %.2f",
                currency, targetRate, condition.getDescription(), currentRate);

        // Actual sending logic (push, SMS, email, etc.)
        System.out.println("📱🔔 [" + userId + "] " + message);

        // TODO: Implement actual sending
        // fcmService.sendPush(userId, "Exchange Rate Alert", message);
        // smsService.sendSMS(getUserPhone(userId), message);
    }

    /**
     * Delete user alert
     */
    public void deleteAlert(String userId, String currency) {
        String alertKey = ALERT_PREFIX + userId + ":" + currency;
        redisTemplate.delete(alertKey);
        System.out.println("🗑️ Alert deleted: " + userId + " - " + currency);
    }

    /**
     * Show all user alerts (for testing)
     */
    public void showAllAlerts() {
        Set<String> allAlerts = redisTemplate.keys(ALERT_PREFIX + "*");
        if (allAlerts == null || allAlerts.isEmpty()) {
            System.out.println("📝 No alerts found.");
            return;
        }

        System.out.println("📝 Alert list:");
        for (String alertKey : allAlerts) {
            String alertData = redisTemplate.opsForValue().get(alertKey);
            System.out.println("  " + alertKey + " -> " + alertData);
        }
    }
}
