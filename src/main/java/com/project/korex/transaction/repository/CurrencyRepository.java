package com.project.korex.transaction.repository;

import com.project.korex.transaction.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}
