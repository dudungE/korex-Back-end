package com.project.korex.user.entity;

import com.project.korex.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 인조키

    @Column(name = "login_id", unique = true, nullable = false, length = 50)
    private String loginId; // 실제 로그인 ID


    private String password;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 30)
    private String birth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role; // 회원 권한 (USER, ADMIN)

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @Builder
    private Users(String loginId, String password, String name, String email, String birth, Role role) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.birth = birth;
        this.role = role;
    }

    public void addRefreshToken(RefreshToken refreshToken) {
        this.refreshTokens.add(refreshToken);
        refreshToken.setUser(this);
    }
}
