package com.project.korex.transaction.repository;

import com.project.korex.transaction.entity.Currency;
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
    List<Transaction> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    /**
     * 특정 사용자 간 마지막 송금일만 조회 (성능 최적화)
     */
    @Query("SELECT t.createdAt FROM Transaction t " +
            "WHERE t.fromUser.id = :fromUserId " +
            "AND t.toUser.id = :toUserId " +
            "AND t.transactionType = :transactionType " +
            "ORDER BY t.createdAt DESC")
    Optional<LocalDateTime> findLastTransferDate(
            @Param("fromUserId") Long fromUserId,
            @Param("toUserId") Long toUserId,
            @Param("transactionType") TransactionType transactionType);

    @Query("SELECT t FROM Transaction t WHERE (t.fromUser.id = :userId OR t.toUser.id = :userId) " +
            "AND (t.fromCurrencyCode = :currency OR t.toCurrencyCode = :currency) " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findByUserIdAndCurrency(@Param("userId") Long userId, @Param("currency") Currency currency);

    @Query("SELECT t FROM Transaction t WHERE (t.fromUser.id = :userId OR t.toUser.id = :userId) " +
            "AND (t.fromCurrencyCode = :currency OR t.toCurrencyCode = :currency) " +
            "AND t.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findMonthlyTransactionsByCurrency(@Param("userId") Long userId,
                                                        @Param("currency") Currency currency,
                                                        @Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Transaction t WHERE (t.fromUser.id = :userId OR t.toUser.id = :userId) " +
            "AND t.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findTransactionsByUserIdAndPeriod(@Param("userId") Long userId,
                                                        @Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM Transaction t WHERE (t.fromUser.id = :userId OR t.toUser.id = :userId) " +
            "AND t.transactionType = :transactionType " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findByUserIdAndTransactionType(@Param("userId") Long userId,
                                                     @Param("transactionType") TransactionType transactionType);
}
