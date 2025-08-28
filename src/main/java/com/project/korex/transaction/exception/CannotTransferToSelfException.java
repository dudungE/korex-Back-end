package com.project.korex.transaction.exception;

import com.project.korex.common.code.ErrorCode;

public class CannotTransferToSelfException extends RuntimeException {

  private final ErrorCode errorCode;

  public CannotTransferToSelfException(ErrorCode errorCode) {
      super(errorCode.getMessage());
      this.errorCode = errorCode;
    }
}
