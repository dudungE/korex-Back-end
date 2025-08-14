package com.project.korex.user.repository.jpa;

import com.project.korex.user.entity.EmailVerificationToken;
import com.project.korex.user.entity.Users;
import com.project.korex.user.enums.VerificationPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByCode(String code);
    Optional<EmailVerificationToken> findTopByEmailOrderByExpiryDateDesc(String email);
    Optional<EmailVerificationToken> findTopByEmailAndPurposeOrderByExpiryDateDesc(String email, VerificationPurpose purpose);
}
