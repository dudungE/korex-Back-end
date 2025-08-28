package com.project.korex.account.exception;

import com.project.korex.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class DuplicateFavoriteException extends RuntimeException {
    private final ErrorCode errorCode;

    public DuplicateFavoriteException(ErrorCode errorCode) {

      super(errorCode.getMessage());
      this.errorCode = errorCode;
    }
}
