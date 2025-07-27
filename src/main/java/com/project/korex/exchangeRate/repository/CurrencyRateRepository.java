package com.project.korex.exchangeRate.repository;

import com.project.korex.exchangeRate.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CurrencyRateRepository extends JpaRepository<ExchangeRate, Long> {

    void deleteByBaseDate(LocalDate baseDate);

    List<ExchangeRate> findByBaseDate(LocalDate baseDate);
}