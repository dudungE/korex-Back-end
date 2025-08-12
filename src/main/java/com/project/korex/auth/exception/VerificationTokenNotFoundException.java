package com.project.korex.auth.exception;

import com.project.korex.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class VerificationTokenNotFoundException extends RuntimeException {

    private final ErrorCode errorCode;

    public VerificationTokenNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}