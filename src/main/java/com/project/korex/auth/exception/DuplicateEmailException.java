package com.project.korex.auth.exception;

import com.project.korex.global.code.ErrorCode;
import lombok.Getter;

/**
 * 회원 가입 시 이메일이 중복될 경우 발생하는 예외
 */
@Getter
public class DuplicateEmailException extends RuntimeException {

    private final ErrorCode errorCode;

    public DuplicateEmailException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
