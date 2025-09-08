package com.project.korex.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendCodeRequest {

    @NotBlank
    @Email
    private String email;

    @Builder.Default
    @NotBlank
    private String purpose = "SIGN_UP";
}