package com.project.korex.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteFriendsResponseDto {

    private Long favoriteId;
    private Long friendUserId;
    private String realName;        // 실제 이름
    private String phoneNumber;     // 마스킹 처리된 전화번호
    private String icon;            // 랜덤 아이콘
    private String lastTransfer;    // 마지막 송금일 (형식화된 텍스트)
    private int displayOrder;   // 표시 순서
}
