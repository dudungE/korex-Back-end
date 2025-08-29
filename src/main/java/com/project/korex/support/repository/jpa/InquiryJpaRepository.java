package com.project.korex.support.repository.jpa;

import com.project.korex.support.entity.Inquiry;
import com.project.korex.support.enums.InquiryStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InquiryJpaRepository extends JpaRepository<Inquiry, Long> {
    Optional<Inquiry> findById(Long id);
    Page<Inquiry> findByUserId(Long userId, Pageable pageable);
    Optional<Inquiry> findByIdAndUserId(Long id, Long userId);

    // 철회/답변 동시성 제어용
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inquiry i where i.id = :id and i.user.id = :userId")
    Optional<Inquiry> findByIdAndUserIdForUpdate(@Param("id") Long id, @Param("userId") Long userId);

    Page<Inquiry> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);

    long countByStatusAndCreatedAtBetween(InquiryStatus status, LocalDateTime start, LocalDateTime end);

    List<Inquiry> findByStatus(InquiryStatus status);

}
