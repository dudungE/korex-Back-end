package com.project.korex.common.exception;

import com.project.korex.common.code.ErrorCode;
import lombok.Getter;

/**
 * 회원을 찾을 수 없을 때 발생하는 예외
 */
@Getter
public class UserNotFoundException extends RuntimeException {

    private final ErrorCode errorCode;

    public UserNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
