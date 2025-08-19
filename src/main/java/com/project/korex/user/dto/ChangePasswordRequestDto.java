package com.project.korex.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChangePasswordRequestDto {
    @NotBlank
    private String currentPassword;
    @NotBlank
    private String newPassword;
    @NotBlank
    private String newPasswordCheck;
}

