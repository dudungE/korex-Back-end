package com.project.korex.alert.repository;

import com.project.korex.alert.entity.AlertSetting;
import com.project.korex.alert.domain.AlertCondition;
import com.project.korex.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlertSettingRepository extends JpaRepository<AlertSetting, Long> {

    // 특정 통화에 대한 활성화된 알람 조회
    List<AlertSetting> findByIsActiveTrueAndCurrencyCode(String currencyCode);

    // 특정 사용자의 활성화된 알람 조회
    List<AlertSetting> findByUserAndIsActiveTrue(Users user);

    // 사용자 ID로 활성화된 알람 조회
    @Query("SELECT a FROM AlertSetting a WHERE a.user.id = :userId AND a.isActive = true")
    List<AlertSetting> findByUserIdAndIsActiveTrue(@Param("userId") Long userId);

    // 특정 사용자의 모든 알람 조회 (비활성 포함)
    List<AlertSetting> findByUserOrderByCreatedAtDesc(Users user);

    // 사용자 ID로 모든 알람 조회
    @Query("SELECT a FROM AlertSetting a WHERE a.user.id = :userId ORDER BY a.createdAt DESC")
    List<AlertSetting> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    // 통화별 활성 알람 개수 조회
    @Query("SELECT COUNT(a) FROM AlertSetting a WHERE a.currencyCode = :currencyCode AND a.isActive = true")
    long countByCurrencyCodeAndIsActiveTrue(@Param("currencyCode") String currencyCode);

    // 조건별 활성 알람 개수 조회
    @Query("SELECT COUNT(a) FROM AlertSetting a WHERE a.condition = :condition AND a.isActive = true")
    long countByConditionAndIsActiveTrue(@Param("condition") AlertCondition condition);

    // 특정 통화와 조건에 대한 활성 알람 조회
    List<AlertSetting> findByIsActiveTrueAndCurrencyCodeAndCondition(String currencyCode, AlertCondition condition);

    // 특정 환율 이상/이하 조건의 활성 알람 조회 (성능 최적화용)
    @Query("SELECT a FROM AlertSetting a WHERE a.isActive = true AND a.currencyCode = :currencyCode " +
            "AND ((a.condition = 'ABOVE' AND a.targetRate <= :currentRate) " +
            "OR (a.condition = 'BELOW' AND a.targetRate >= :currentRate))")
    List<AlertSetting> findTriggeredAlerts(@Param("currencyCode") String currencyCode,
                                           @Param("currentRate") BigDecimal currentRate);

    // 사용자의 특정 통화 알람 중복 확인
    @Query("SELECT a FROM AlertSetting a WHERE a.user.id = :userId AND a.currencyCode = :currencyCode " +
            "AND a.targetRate = :targetRate AND a.condition = :condition AND a.isActive = true")
    Optional<AlertSetting> findDuplicateAlert(@Param("userId") Long userId,
                                              @Param("currencyCode") String currencyCode,
                                              @Param("targetRate") BigDecimal targetRate,
                                              @Param("condition") AlertCondition condition);
}
