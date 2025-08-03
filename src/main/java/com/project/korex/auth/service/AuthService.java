package com.project.korex.auth.service;

import com.project.korex.auth.dto.JoinRequestDto;
import com.project.korex.auth.dto.LoginRequestDto;
import com.project.korex.auth.dto.UserInfoDto;
import com.project.korex.auth.exception.*;
import com.project.korex.global.code.ErrorCode;
import com.project.korex.global.exception.UserNotFoundException;
import com.project.korex.global.security.jwt.JwtProvider;
import com.project.korex.user.entity.EmailVerificationToken;
import com.project.korex.user.entity.RefreshToken;
import com.project.korex.user.entity.Role;
import com.project.korex.user.entity.User;
import com.project.korex.user.enums.RoleType;
import com.project.korex.user.repository.jpa.EmailVerificationTokenRepository;
import com.project.korex.user.repository.jpa.RefreshTokenRepository;
import com.project.korex.user.repository.jpa.RoleJpaRepository;
import com.project.korex.user.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserJpaRepository userJpaRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final JwtProvider jwtProvider;

    @Value("${custom.site-host}")
    private String siteHost;

    public void joinMember(JoinRequestDto joinRequestDto) {
        // 비밀번호 확인 검증
        if (!joinRequestDto.getPassword().equals(joinRequestDto.getPasswordCheck())) {
            throw new PasswordMismatchException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 아이디 중복 검증
        String loginId = joinRequestDto.getLoginId();
        if (userJpaRepository.existsByLoginId(loginId)) {
            throw new PasswordMismatchException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        String email = joinRequestDto.getEmail();
        // 이메일 중복 검증
        if (userJpaRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(ErrorCode.DUPLICATE_EMAIL);
        }
        // 유저 타입 가져오기
        Role role = roleJpaRepository.findByRoleName(RoleType.USER.getKey())
                .orElseThrow(() -> new RoleNotFoundException(ErrorCode.ROLE_NOT_FOUND));

        // 유저 빌더 엔티티 생성(암호화 포함)
        User newUser = User.builder()
                .loginId(joinRequestDto.getLoginId())
                .password(passwordEncoder.encode(joinRequestDto.getPassword()))
                .name(joinRequestDto.getName())
                .email(joinRequestDto.getEmail())
                .role(role)
                .enabled(false)
                .build();

        // 유저 저장
        userJpaRepository.save(newUser);

        // 인증 토큰 생성
        String token = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken = new EmailVerificationToken(token, newUser);
        tokenRepository.save(verificationToken);

        log.info("회원가입 성공 ID : {}, loginId : {}", newUser.getId(), newUser.getLoginId());
    }

    public Map<String, Object> login(LoginRequestDto loginRequestDto) {
        String loginId = loginRequestDto.getLoginId();
        User findUser = userJpaRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));


        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), findUser.getPassword())) {
            throw new LoginFailedException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 액세스 토큰 생성
        String accessToken = jwtProvider.createAccessToken(findUser.getLoginId(), findUser.getRole().getRoleName());

        // 리프레시 토큰 생성
        String refreshToken = jwtProvider.createRefreshToken(findUser.getLoginId(), findUser.getRole().getRoleName());

        // 리프레시 토큰의 만료 시간
        LocalDateTime refreshTokenExpireTime = LocalDateTime.now().plusSeconds(JwtProvider.REFRESH_TOKEN_EXPIRE_TIME / 1000);

        // 토큰 빌더 엔티티 생성
        RefreshToken newRefreshToken = RefreshToken.builder()
                .user(findUser)
                .token(refreshToken)
                .expiredAt(refreshTokenExpireTime)
                .build();

        // 토큰 저장
        refreshTokenRepository.save(newRefreshToken);


        // 사용자 정보 생성
        UserInfoDto userInfo = new UserInfoDto(
                findUser.getLoginId(),
                findUser.getRole().getRoleName()
        );

        Map<String, Object> authData = new HashMap<>();
        authData.put("accessToken", accessToken);
        authData.put("refreshToken", refreshToken);
        authData.put("userInfo", userInfo);

        log.info("로그인 성공 및 토큰 생성. loginId = {}", loginId);
        return authData;
    }

    public void logout(String refreshToken) {
        // 토큰 찾고 있으면 제거
        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            refreshTokenRepository.delete(token);
            log.info("리프레시 토큰을 DB에서 제거했습니다. user: {}", token.getUser().getLoginId());
        });
    }

    public HashMap<String, String> reissue(String refreshToken) {
        // 리프레시 토큰 유효성 검증 (만료, 위조 등)
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException(ErrorCode.INVALID_TOKEN);
        }

        // DB에 저장된 토큰인지, 그리고 만료되지 않았는지 확인
        RefreshToken findRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new TokenNotFoundException(ErrorCode.TOKEN_NOT_FOUND));

        // 기존 리프레시 토큰 삭제
        refreshTokenRepository.delete(findRefreshToken);

        // 토큰에서 사용자 정보(loginId, role) 추출 및 검증(위에서 진행하긴 했음)
        String loginId = jwtProvider.getLoginId(refreshToken);
        String role = jwtProvider.getRole(refreshToken);

        // 리프레시 토큰 엔티티 저장하기 위해 추출
        User findUser = userJpaRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtProvider.createAccessToken(loginId, role);
        // 새로운 리프레시 토큰 생성
        String newRefreshToken = jwtProvider.createRefreshToken(loginId, role);
        // 만료시간(DB저장 용)
        LocalDateTime refreshTokenExpireTime = LocalDateTime.now().plusSeconds(JwtProvider.REFRESH_TOKEN_EXPIRE_TIME / 1000);

        // 토큰 빌더 엔티티 생성
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .user(findUser)
                .token(newRefreshToken)
                .expiredAt(refreshTokenExpireTime)
                .build();
        // DB 저장
        refreshTokenRepository.save(newRefreshTokenEntity);

        log.info("액세스 토큰을 재발급했습니다. user: {}", loginId);
        log.info("리프레시 토큰을 재발급했습니다. user: {}", loginId);

        // 컨트롤러 리턴용 맵
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", newAccessToken);
        tokenMap.put("refreshToken", newRefreshToken);
        // 컨트롤러에 새로운 액세스, 리프레시 토큰 반환
        return tokenMap;
    }
}
