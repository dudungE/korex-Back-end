package com.project.korex.user.service;

import com.project.korex.auth.exception.LoginFailedException;
import com.project.korex.auth.exception.PasswordMismatchException;
import com.project.korex.common.code.ErrorCode;
import com.project.korex.common.exception.UserNotFoundException;
import com.project.korex.user.dto.ChangePasswordRequestDto;
import com.project.korex.user.entity.Users;
import com.project.korex.user.repository.jpa.RefreshTokenRepository;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {

    @Mock
    private UserJpaRepository userJpaRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private UserService userService;

    private Users testUser;
    private final String loginId = "testuser";
    private final String currentPassword = "oldPassword123!";
    private final String newPassword = "newPassword456@";
    private final String encodedCurrentPassword = "encodedOldPassword123!";
    private final String encodedNewPassword = "encodedNewPassword456@";

    @BeforeEach
    void setUp() {
        testUser = Users.builder()
                .loginId(loginId)
                .password(encodedCurrentPassword)
                .build();
    }

    @Test
    @DisplayName("비밀번호 변경이 정상적으로 성공한다")
    void changePassword_success_shouldUpdatePassword() {
        // given
        com.project.korex.user.dto.ChangePasswordRequestDto requestDto = new com.project.korex.user.dto.ChangePasswordRequestDto(
                currentPassword, newPassword, newPassword);

        when(userJpaRepository.findByLoginId(loginId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(requestDto.getCurrentPassword(), testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(requestDto.getNewPassword(), testUser.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(requestDto.getNewPassword())).thenReturn(encodedNewPassword);

        // when
        userService.changePassword(loginId, requestDto);

        // then
        // 1. 비밀번호가 새 비밀번호로 업데이트되었는지 검증
        verify(userJpaRepository).save(any(Users.class));
        // 2. Refresh Token이 삭제되었는지 검증
        verify(refreshTokenRepository).deleteByUser(testUser);
    }

    @Test
    @DisplayName("사용자를 찾을 수 없을 때 비밀번호 변경에 실패한다")
    void changePassword_userNotFound_shouldThrowException() {
        // given
        ChangePasswordRequestDto requestDto = new ChangePasswordRequestDto(
                currentPassword, newPassword, newPassword);

        when(userJpaRepository.findByLoginId(anyString())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.changePassword(loginId, requestDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());

        // 실패 시, save나 deleteByUser 메서드가 호출되지 않아야 함
        verify(userJpaRepository, never()).save(any(Users.class));
        verify(refreshTokenRepository, never()).deleteByUser(any(Users.class));
    }

    @Test
    @DisplayName("현재 비밀번호가 일치하지 않을 때 변경에 실패한다")
    void changePassword_passwordMismatch_shouldThrowException() {
        // given
        ChangePasswordRequestDto requestDto = new ChangePasswordRequestDto(
                "wrongPassword", newPassword, newPassword);

        when(userJpaRepository.findByLoginId(loginId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", testUser.getPassword())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.changePassword(loginId, requestDto))
                .isInstanceOf(LoginFailedException.class)
                .hasMessageContaining(ErrorCode.PASSWORD_MISMATCH.getMessage());

        verify(userJpaRepository, never()).save(any(Users.class));
        verify(refreshTokenRepository, never()).deleteByUser(any(Users.class));
    }

    @Test
    @DisplayName("새 비밀번호와 확인 비밀번호가 일치하지 않을 때 변경에 실패한다")
    void changePassword_newPasswordMismatch_shouldThrowException() {
        // given
        ChangePasswordRequestDto requestDto = new ChangePasswordRequestDto(
                currentPassword, newPassword, "diffPassword");

        when(userJpaRepository.findByLoginId(loginId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(requestDto.getCurrentPassword(), testUser.getPassword())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.changePassword(loginId, requestDto))
                .isInstanceOf(PasswordMismatchException.class)
                .hasMessageContaining(ErrorCode.PASSWORD_MISMATCH.getMessage());

        verify(userJpaRepository, never()).save(any(Users.class));
        verify(refreshTokenRepository, never()).deleteByUser(any(Users.class));
    }
}
