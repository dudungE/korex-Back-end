package com.project.korex.support.repository.jpa;

import com.project.korex.support.entity.InquiryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryAnswerJpaRepository extends JpaRepository<InquiryAnswer, Long> {
    boolean existsByInquiryId(Long inquiryId);
}
