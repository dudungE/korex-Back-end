package com.project.korex.user.entity;

import com.project.korex.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "refresh_token", nullable = false, length = 512)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Column(length = 100)
    private String deviceInfo;

    @Builder
    private RefreshToken(Users user, String token, LocalDateTime expiredAt, String deviceInfo) {
        this.user = user;
        this.token = token;
        this.expiredAt = expiredAt;
        this.deviceInfo = deviceInfo;
    }

    protected void setUser(Users user) {
        this.user = user;
    }
}
