package com.project.korex.common.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.WeakKeyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtProvider {

    private final SecretKey secretKey;

    // 토큰 만료 시간을 상수로 관리 (밀리초 단위)
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 30분
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14; // 14일

    public JwtProvider(@Value("${spring.jwt.secret}") String secret) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new WeakKeyException("spring.jwt.secret must be at least 32 bytes for HS256");
        }
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getLoginId(String token) {
        return parseClaims(token).get("loginId", String.class);
    }

    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getAuthorities(String token) {
        Object raw = parseClaims(token).get("authorities");
        if (raw instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        if (raw instanceof String s) {
            // 혹시 문자열로 저장됐다면 콤마 분리 fallback
            return Arrays.stream(s.split(",")).map(String::trim).filter(t -> !t.isEmpty()).toList();
        }
        return List.of();
    }

    public boolean getEmailVerified(String token) {
        Boolean b = parseClaims(token).get("emailVerified", Boolean.class);
        return Boolean.TRUE.equals(b);
    }

    public String createAccessToken(String loginId, List<String> authorities) {
        return createToken(loginId, authorities, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken(String loginId, List<String> authorities) {
        return createToken(loginId, authorities, REFRESH_TOKEN_EXPIRE_TIME);
    }


    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(this.secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 토큰 생성 로직 공통화
    private String createToken(String loginId, List<String> authorities, long expiredMs) {
        List<String> auths = authorities == null ? List.of() : authorities;
        boolean emailVerified = auths.contains("VERIFIED");
        // legacy 호환을 위해 role도 유지(첫 번째 ROLE_* 또는 null)
        String role = auths.stream()
                .filter(a -> a != null && a.startsWith("ROLE_"))
                .findFirst()
                .orElse(null);

        return Jwts.builder()
                .claim("loginId", loginId)
                .claim("role", role)
                .claim("authorities", auths)
                .claim("emailVerified", emailVerified)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.", e);
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.", e);
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 잘못되었습니다.", e);
        }
        return false;
    }

}
