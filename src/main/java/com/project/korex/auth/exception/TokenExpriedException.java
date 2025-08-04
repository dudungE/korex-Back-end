package com.project.korex.auth.exception;

import com.project.korex.global.code.ErrorCode;
import lombok.Getter;

@Getter
public class TokenExpriedException extends RuntimeException {

  private final ErrorCode errorCode;

  public TokenExpriedException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}