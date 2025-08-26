package com.project.korex.support.exception;

import com.project.korex.common.code.ErrorCode;
import lombok.Getter;

/**
 * 문의 내역을 찾을 수 없을 때 발생하는 예외
 */
@Getter
public class InquiryNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public InquiryNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
