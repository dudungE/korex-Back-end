package com.project.korex.account.entity;

import com.project.korex.common.BaseEntity;
import com.project.korex.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_friends")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteFriend extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long id;

    // 연관관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_user_id")
    private Users friend;

    @Column(name = "nickname", length = 100)
    private String nickname; // 사용자가 지정한 닉네임

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 1;

    @Column(name = "last_transfer_date")
    private LocalDateTime lastTransferDate;

    // 비즈니스 메서드
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public void updateLastTransferDate(LocalDateTime transferDate) {
        this.lastTransferDate = transferDate;
    }
}