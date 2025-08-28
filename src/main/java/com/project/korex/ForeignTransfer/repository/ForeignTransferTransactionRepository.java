package com.project.korex.ForeignTransfer.repository;

import com.project.korex.ForeignTransfer.entity.ForeignTransferTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForeignTransferTransactionRepository extends JpaRepository<ForeignTransferTransaction, Long> {

    // 거래 ID로 특정 외화 송금 거래를 조회
    Optional<ForeignTransferTransaction> findById(Long id);

}
