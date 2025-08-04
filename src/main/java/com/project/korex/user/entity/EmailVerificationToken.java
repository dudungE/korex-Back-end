package com.project.korex.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 6)
    private String code;  // 6자리 숫자 코드

    @Column(nullable = false)
    private boolean verified = false;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Builder
    public EmailVerificationToken(String code, LocalDateTime expiryDate) {
        this.code = code;
        this.expiryDate = expiryDate;
    }

    public EmailVerificationToken(String code) {
        this.code = code;
    }

    public void markAsVerified() {
        this.verified = true;
    }
}


