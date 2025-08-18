package com.project.korex.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyInfoResponseDto {

    @NotBlank
    private String name;

    @NotBlank
    private String loginId;

    @NotBlank @Email
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String birth;

    @NotBlank
    private boolean emailVerified;
}
