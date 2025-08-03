package com.project.korex.auth.exception;

import com.project.korex.global.code.ErrorCode;
import lombok.Getter;

@Getter
public class RoleNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public RoleNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
