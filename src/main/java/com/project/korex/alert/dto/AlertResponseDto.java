package com.project.korex.alert.dto;

import com.project.korex.alert.entity.AlertSetting;
import com.project.korex.alert.domain.AlertCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertResponseDto {
    private Long id;
    private String currencyCode;
    private BigDecimal targetRate;
    private AlertCondition condition;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // 사용자 정보 (프록시 문제 해결)
    private Long userId;
    private String userEmail;
    private String userName;

    // 엔티티에서 DTO로 변환하는 정적 메소드
    public static AlertResponseDto from(AlertSetting alertSetting) {
        return AlertResponseDto.builder()
                .id(alertSetting.getId())
                .currencyCode(alertSetting.getCurrencyCode())
                .targetRate(alertSetting.getTargetRate())
                .condition(alertSetting.getCondition())
                .isActive(alertSetting.getIsActive())
                .createdAt(alertSetting.getCreatedAt())
                .userId(alertSetting.getUser().getId())
                .userEmail(alertSetting.getUser().getEmail())
                .userName(alertSetting.getUser().getName())
                .build();
    }
}
