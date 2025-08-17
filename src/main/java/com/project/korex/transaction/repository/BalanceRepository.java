package com.project.korex.transaction.repository;

import com.project.korex.transaction.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceRepository extends JpaRepository<Balance, Long> {

}
