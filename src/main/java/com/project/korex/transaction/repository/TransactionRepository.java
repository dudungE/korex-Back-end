package com.project.korex.transaction.repository;

import com.project.korex.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.fromUser.id = :userId OR t.toUser.id = :userId ORDER BY t.createdAt DESC")
    List<Transaction> findByFromUserIdOrToUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("userId") Long userId2);

    List<Transaction> findByFromUserIdOrderByCreatedAtDesc(Long fromUserId);
}

