package com.project.korex.transaction.repository;

import com.project.korex.transaction.entity.Transaction;
import com.project.korex.transaction.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.fromUser.id = :userId OR t.toUser.id = :userId ORDER BY t.createdAt DESC")
    List<Transaction> findByFromUserIdOrToUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("userId") Long userId2);

    /**
     * 특정 사용자 간 마지막 송금일만 조회 (성능 최적화)
     */
    @Query("SELECT t.createdAt FROM Transaction t " +
            "WHERE t.fromUser = :fromUserId " +
            "AND t.toUser = :toUserId " +
            "AND t.transactionType = :transactionType " +
            "ORDER BY t.createdAt DESC " +
            "LIMIT 1")
    Optional<LocalDateTime> findLastTransferDate(
            @Param("fromUserId") Long fromUserId,
            @Param("toUserId") Long toUserId,
            @Param("transactionType") TransactionType transactionType);
    List<Transaction> findByFromUserIdOrderByCreatedAtDesc(Long fromUserId);
}

