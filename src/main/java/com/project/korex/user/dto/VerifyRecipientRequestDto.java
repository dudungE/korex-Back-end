package com.project.korex.user.dto;

import lombok.Data;

@Data
public class VerifyRecipientRequestDto {

    private Long currentUserId;
    private String name;
    private String phone;
}
