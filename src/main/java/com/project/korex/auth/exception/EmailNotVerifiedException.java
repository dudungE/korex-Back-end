package com.project.korex.auth.exception;

import com.project.korex.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class EmailNotVerifiedException extends RuntimeException {
    private final ErrorCode errorCode;

    public EmailNotVerifiedException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
