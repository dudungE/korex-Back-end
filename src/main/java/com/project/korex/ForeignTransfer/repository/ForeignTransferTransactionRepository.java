package com.project.korex.ForeignTransfer.repository;

import com.project.korex.ForeignTransfer.entity.ForeignTransferTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForeignTransferTransactionRepository extends JpaRepository<ForeignTransferTransaction, Long> {

    Optional<ForeignTransferTransaction> findById(Long id);

}
