package com.project.korex.alert.repository;

import com.project.korex.alert.entity.AlertHistory;
import com.project.korex.alert.entity.AlertSetting;
import com.project.korex.alert.domain.AlertCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertHistoryRepository extends JpaRepository<AlertHistory, Long> {

    // 특정 알람 설정의 히스토리 조회
    List<AlertHistory> findByAlertSettingOrderBySentAtDesc(AlertSetting alertSetting);

    // 특정 사용자의 알람 히스토리 조회 (페이징)
    @Query("SELECT h FROM AlertHistory h WHERE h.alertSetting.user.id = :userId ORDER BY h.sentAt DESC")
    Page<AlertHistory> findByUserIdOrderBySentAtDesc(@Param("userId") Long userId, Pageable pageable);

    // 특정 사용자의 모든 알람 히스토리 조회
    @Query("SELECT h FROM AlertHistory h WHERE h.alertSetting.user.id = :userId ORDER BY h.sentAt DESC")
    List<AlertHistory> findByUserIdOrderBySentAtDesc(@Param("userId") Long userId);

    // 특정 기간 내 알람 히스토리 조회
    @Query("SELECT h FROM AlertHistory h WHERE h.sentAt BETWEEN :startDate AND :endDate ORDER BY h.sentAt DESC")
    List<AlertHistory> findByPeriodOrderBySentAtDesc(@Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);

    // 실패한 알람 조회
    List<AlertHistory> findByIsSuccessFalseOrderBySentAtDesc();

    // 특정 통화의 알람 히스토리 조회
    List<AlertHistory> findByCurrencyCodeOrderBySentAtDesc(String currencyCode);

    // 특정 조건의 알람 히스토리 조회
    List<AlertHistory> findByConditionOrderBySentAtDesc(AlertCondition condition);

    // 통화별 알람 발송 통계
    @Query("SELECT h.currencyCode, COUNT(h), AVG(h.triggeredRate) " +
            "FROM AlertHistory h " +
            "WHERE h.sentAt >= :fromDate " +
            "GROUP BY h.currencyCode")
    List<Object[]> getAlertStatisticsByCurrency(@Param("fromDate") LocalDateTime fromDate);

    // 최근 N일간의 알람 히스토리 조회
    @Query("SELECT h FROM AlertHistory h WHERE h.sentAt >= :fromDate ORDER BY h.sentAt DESC")
    List<AlertHistory> findRecentAlerts(@Param("fromDate") LocalDateTime fromDate);

    // 특정 사용자의 최근 알람 N개 조회
    @Query("SELECT h FROM AlertHistory h WHERE h.alertSetting.user.id = :userId ORDER BY h.sentAt DESC")
    List<AlertHistory> findTopNByUserId(@Param("userId") Long userId, Pageable pageable);
}
