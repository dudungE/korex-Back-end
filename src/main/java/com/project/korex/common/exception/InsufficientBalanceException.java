package com.project.korex.common.exception;

import com.project.korex.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class InsufficientBalanceException extends RuntimeException {
    private final ErrorCode errorCode;

    public InsufficientBalanceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
