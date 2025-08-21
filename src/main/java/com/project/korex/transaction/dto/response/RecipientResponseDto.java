package com.project.korex.transaction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipientResponseDto {
    private Long userId;
    private String name;
    private String phone;
    private boolean exists;        // 회원 존재 여부
    private boolean isFriend;      // 친구 여부
}
