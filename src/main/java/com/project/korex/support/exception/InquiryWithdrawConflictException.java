package com.project.korex.support.exception;

import com.project.korex.common.code.ErrorCode;
import lombok.Getter;

/**
 * 문의 철회와 답변 등록 충돌 시 발생하는 예외
 */
@Getter
public class InquiryWithdrawConflictException extends RuntimeException {
  private final ErrorCode errorCode;

  public InquiryWithdrawConflictException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
