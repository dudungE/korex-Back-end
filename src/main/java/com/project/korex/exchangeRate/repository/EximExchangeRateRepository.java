package com.project.korex.exchangeRate.repository;

import com.project.korex.exchangeRate.entity.EximExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EximExchangeRateRepository extends JpaRepository<EximExchangeRate, Long> {

    // 날짜 기준 삭제
    void deleteByBaseDate(LocalDate baseDate);

    // 날짜별 조회
    List<EximExchangeRate> findByBaseDate(LocalDate baseDate);

}
