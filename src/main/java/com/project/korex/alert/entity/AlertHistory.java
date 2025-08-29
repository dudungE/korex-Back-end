package com.project.korex.alert.entity;

import com.project.korex.alert.domain.AlertCondition;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlertHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // AlertSetting과의 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_setting_id", nullable = false)
    private AlertSetting alertSetting;

    @Column(name = "currency_code", nullable = false, length = 10)
    private String currencyCode;

    @Column(name = "target_rate", nullable = false, precision = 10, scale = 4)
    private BigDecimal targetRate;

    @Column(name = "triggered_rate", nullable = false, precision = 10, scale = 4)
    private BigDecimal triggeredRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type", nullable = false)
    private AlertCondition condition;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "alert_type", length = 20)
    private String alertType = "EMAIL";

    @Column(name = "is_success", nullable = false)
    private Boolean isSuccess = true;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @PrePersist
    protected void onCreate() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
    }

    // 이메일 조회 메소드
    public String getUserEmail() {
        return alertSetting != null && alertSetting.getUser() != null ?
                alertSetting.getUser().getEmail() : null;
    }

    // 편의 생성자 - 성공 케이스
    public AlertHistory(AlertSetting alertSetting, BigDecimal triggeredRate) {
        this.alertSetting = alertSetting;
        this.currencyCode = alertSetting.getCurrencyCode();
        this.targetRate = alertSetting.getTargetRate();
        this.triggeredRate = triggeredRate;
        this.condition = alertSetting.getCondition();
        this.sentAt = LocalDateTime.now();
        this.isSuccess = true;
    }


    // 편의 생성자 - 실패 케이스
    public AlertHistory(AlertSetting alertSetting, BigDecimal triggeredRate, String errorMessage) {
        this.alertSetting = alertSetting;
        this.currencyCode = alertSetting.getCurrencyCode();
        this.targetRate = alertSetting.getTargetRate();
        this.triggeredRate = triggeredRate;
        this.condition = alertSetting.getCondition();
        this.sentAt = LocalDateTime.now();
        this.isSuccess = false;
        this.errorMessage = errorMessage;
    }

    public String getConditionDescription() {
        return condition != null ? condition.getDescription() : "";
    }
}
