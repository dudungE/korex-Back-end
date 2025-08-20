package com.project.korex.support.repository.jpa;

import com.project.korex.support.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InquiryJpaRepository extends JpaRepository<Inquiry, Long> {
    Page<Inquiry> findByUserId(Long userId, Pageable pageable);
    Optional<Inquiry> findByIdAndUserId(Long id, Long userId);
}
