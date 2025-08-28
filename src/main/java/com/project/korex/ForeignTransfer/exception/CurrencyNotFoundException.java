package com.project.korex.ForeignTransfer.exception;

import com.project.korex.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class CurrencyNotFoundException extends RuntimeException {

    private final ErrorCode errorCode;

    public CurrencyNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
