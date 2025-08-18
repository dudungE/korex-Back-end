package com.project.korex.user.dto;

import lombok.Data;

@Data
public class MyInfoUpdateRequestDto {

    private String email;

    private String phone;

    private String birth;
}
