package com.project.korex.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @Schema(description = "이메일", example = "user@example.com", required = true)
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "인증 코드를 입력해주세요.")
    private String code;

    @Schema(description = "새 비밀번호", example = "P@ssw0rd!", required = true)
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String newPassword;

//    @Schema(description = "새 비밀번호 확인", example = "P@ssw0rd!", required = true)
//    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
//    private String confirmPassword;
}
