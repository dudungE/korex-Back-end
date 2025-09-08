package com.project.korex.transaction.exception;

import com.project.korex.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class ExchangeException extends RuntimeException {

    private final ErrorCode errorCode;
    public ExchangeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
