package com.project.korex.auth.exception;

import com.project.korex.global.code.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidVerificationCodeException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidVerificationCodeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
