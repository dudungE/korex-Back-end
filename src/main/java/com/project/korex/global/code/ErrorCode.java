package com.project.korex.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common (Cxxx)
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "유효하지 않은 입력 값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "지원하지 않는 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "서버 내부 오류가 발생했습니다."),

    // User (Uxxx)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "해당 회원을 찾을 수 없습니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "u002", "이미 사용 중인 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U003", "이미 사용 중인 이메일입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "U004", "비밀번호가 일치하지 않습니다."),

    // Auth (Axxx)
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A001", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A002", "만료된 토큰입니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "A003", "인증에 실패했습니다. 유효한 자격 증명이 필요합니다."),
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "A004", "액세스 토큰이 필요합니다."),
    VERIFICATION_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "A005", "이메일 인증 토큰이 존재하지 않거나 만료되었습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A006", "인증 토큰이 만료되었습니다."),
    INVALID_CODE(HttpStatus.BAD_REQUEST, "A007", "인증 코드가 일치하지 않습니다."),
    EMAIL_VERIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "A008", "이메일 인증 정보가 존재하지 않습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "A009", "이메일 인증이 완료되지 않았습니다."),

    // Role (RXXX)
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "역할을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
