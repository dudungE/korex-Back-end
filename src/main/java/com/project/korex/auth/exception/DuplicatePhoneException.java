package com.project.korex.auth.exception;

import com.project.korex.common.code.ErrorCode;
import lombok.Getter;

/**
 * 회원 가입 시 휴대폰 번호가 중복될 경우 발생하는 예외
 */
@Getter
public class DuplicatePhoneException extends RuntimeException {

    private final ErrorCode errorCode;

    public DuplicatePhoneException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}