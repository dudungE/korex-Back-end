package com.project.korex.user.entity;

import com.project.korex.common.BaseEntity;
import com.project.korex.user.enums.VerificationPurpose;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerificationToken extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false)
    private boolean verified = false;

    @Enumerated(EnumType.STRING)
    private VerificationPurpose purpose;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Builder
    private EmailVerificationToken(String email, String code, LocalDateTime expiryDate, VerificationPurpose purpose) {
        this.email = email;
        this.code = code;
        this.expiryDate = expiryDate;
        this.purpose = purpose;
        this.verified = false; // 명시(선택이지만 안전)
    }

    public void markAsVerified() { this.verified = true; }
}
