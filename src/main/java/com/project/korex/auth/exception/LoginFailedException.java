package com.project.korex.auth.exception;

import com.project.korex.common.code.ErrorCode;
import lombok.Getter;

/**
 * 로그인 실패 했을 때 발생하는 예외
 */
@Getter
public class LoginFailedException extends RuntimeException {

  private final ErrorCode errorCode;
  private final Integer failCount;
  private final Boolean restricted;

  public LoginFailedException(ErrorCode code, Integer failCount, Boolean restricted) {
    super(code.getMessage());
    this.errorCode = code;
    this.failCount = failCount;
    this.restricted = restricted;
  }
}
