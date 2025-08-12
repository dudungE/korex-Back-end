package com.project.korex.global.advice;

import com.project.korex.auth.exception.*;
import com.project.korex.global.code.ErrorCode;
import com.project.korex.global.dto.ErrorResponseDto;
import com.project.korex.global.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 NOT_FOUND - 리소스를 찾을 수 없음
    @ExceptionHandler({
            UserNotFoundException.class,
            RoleNotFoundException.class
    })
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(RuntimeException ex, HttpServletRequest request) {
        ErrorCode errorCode = getErrorCodeFromException(ex);

        log.warn("[NotFoundException] {} - Path: {}", errorCode.getMessage(), request.getRequestURI());

        ErrorResponseDto response = ErrorResponseDto.of(errorCode, request.getRequestURI());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    // 409 CONFLICT - 중복/충돌
    @ExceptionHandler({
            DuplicateLoginIdException.class,
            DuplicateEmailException.class
    })
    public ResponseEntity<ErrorResponseDto> handleConflictException(RuntimeException ex, HttpServletRequest request) {
        ErrorCode errorCode = getErrorCodeFromException(ex);

        log.warn("[ConflictException] {} - Path: {}", errorCode.getMessage(), request.getRequestURI());

        ErrorResponseDto response = ErrorResponseDto.of(errorCode, request.getRequestURI());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    // 400 BAD_REQUEST - 잘못된 요청/비즈니스 로직 위반
    @ExceptionHandler({
            PasswordMismatchException.class,
            LoginFailedException.class,
            TokenNotFoundException.class,
            VerificationTokenNotFoundException.class,
            InvalidVerificationCodeException.class,
            EmailNotVerifiedException.class
    })
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(RuntimeException ex, HttpServletRequest request) {
        ErrorCode errorCode = getErrorCodeFromException(ex);

        log.warn("[BadRequestException] {} - Path: {}", errorCode.getMessage(), request.getRequestURI());

        ErrorResponseDto response = ErrorResponseDto.of(errorCode, request.getRequestURI());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    // 401 UNAUTHORIZED - 인증 실패
    @ExceptionHandler({
            InvalidTokenException.class,
            TokenExpriedException.class
    })
    public ResponseEntity<ErrorResponseDto> handleUnauthorizedException(RuntimeException ex, HttpServletRequest request) {
        ErrorCode errorCode = getErrorCodeFromException(ex);

        log.warn("[UnauthorizedException] {} - Path: {}", errorCode.getMessage(), request.getRequestURI());

        ErrorResponseDto response = ErrorResponseDto.of(errorCode, request.getRequestURI());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    // Validation 오류 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

        log.warn("[ValidationException] {} - Path: {}", errorCode.getMessage(), request.getRequestURI());

        ErrorResponseDto response = ErrorResponseDto.of(errorCode, request.getRequestURI(), ex.getBindingResult().getFieldErrors());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    // 전체 예외 처리 (최후 수단)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleInternalServerError(Exception ex, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        log.error("서버 오류 발생: {}", ex.getMessage(), ex);

        ErrorResponseDto response = ErrorResponseDto.of(errorCode, request.getRequestURI());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    // ErrorCode 추출 헬퍼 메서드
    private ErrorCode getErrorCodeFromException(RuntimeException ex) {
        try {
            return (ErrorCode) ex.getClass().getMethod("getErrorCode").invoke(ex);
        } catch (Exception e) {
            log.error("ErrorCode 추출 실패: {}", ex.getClass().getSimpleName());
            return ErrorCode.INTERNAL_SERVER_ERROR;
        }
    }
}
