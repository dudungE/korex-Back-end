package com.project.korex.alert.entity;

import com.project.korex.alert.domain.AlertCondition;
import com.project.korex.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alert_settings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlertSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User 엔티티와의 다대일 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "currency_code", nullable = false, length = 10)
    private String currencyCode;

    @Column(name = "target_rate", nullable = false, precision = 10, scale = 4)
    private BigDecimal targetRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type", nullable = false)
    private AlertCondition condition;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // AlertHistory와의 일대다 관계 설정 (cascade 포함)
    @OneToMany(mappedBy = "alertSetting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlertHistory> alertHistories = new ArrayList<>();


    // 편의 생성자
    @Builder
    public AlertSetting(Users user, String currencyCode, BigDecimal targetRate, AlertCondition condition) {
        this.user = user;
        this.currencyCode = currencyCode;
        this.targetRate = targetRate;
        this.condition = condition;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 비즈니스 로직 메소드
    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public String getUserEmail() {
        return user != null ? user.getEmail() : null;
    }

    public String getUserName() {
        return user != null ? user.getName() : null;
    }

    public String getConditionDescription() {
        return condition != null ? condition.getDescription() : "";
    }

    // 알람 조건 체크 메소드
    public boolean shouldTrigger(BigDecimal currentRate) {
        if (currentRate == null || targetRate == null || condition == null) {
            return false;
        }

        switch (condition) {
            case ABOVE:
                return currentRate.compareTo(targetRate) >= 0;
            case BELOW:
                return currentRate.compareTo(targetRate) <= 0;
            default:
                return false;
        }
    }
}
