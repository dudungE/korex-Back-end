package com.project.korex.user.controller;

import com.project.korex.common.security.user.CustomUserPrincipal;
import com.project.korex.user.dto.ChangePasswordRequestDto;
import com.project.korex.user.dto.MyInfoResponseDto;
import com.project.korex.user.dto.MyInfoUpdateRequestDto;
import com.project.korex.user.dto.VerifyRecipientRequestDto;
import com.project.korex.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    /**
     * 로그인한 사용자의 개인정보 조회
     */
    @GetMapping("/myinfo")
    public ResponseEntity<MyInfoResponseDto> getProfile(@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        String loginId = customUserPrincipal.getName();
        MyInfoResponseDto profileResponseDto = userService.getProfile(loginId);
        return ResponseEntity.ok(profileResponseDto);
    }

    /**
     *  개인정보 수정
     */
    @PostMapping("/myinfo")
    public ResponseEntity<MyInfoResponseDto> updateProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody MyInfoUpdateRequestDto req) {
        String loginId = principal.getName();
        userService.updateUserInfo(loginId, req);
        return ResponseEntity.ok(userService.getProfile(loginId));
    }

    /**
     * 비밀번호 변경
     */
    @PostMapping("/pw-change")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequestDto req) {
        userService.changePassword(principal.getName(), req);
        return ResponseEntity.noContent().build();
    }

    // 1. 이름 존재 여부 확인
    @GetMapping("/exists")
    public boolean existsByName(@RequestParam String name) {
        return userService.existsByName(name);  // Service 메서드 호출
    }

    @PostMapping("/verify-recipient")
    public boolean verifyRecipient(@RequestBody VerifyRecipientRequestDto dto) {
        return userService.verifyRecipient(dto);  // Service 메서드 호출
    }
}
