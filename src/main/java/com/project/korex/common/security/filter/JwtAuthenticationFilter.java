package com.project.korex.common.security.filter;

import com.project.korex.common.security.jwt.JwtProvider;
import com.project.korex.common.security.service.CustomUserDetailsService;
import com.project.korex.common.security.user.CustomUserPrincipal;
import com.project.korex.common.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final CookieUtil cookieUtil;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, CustomUserDetailsService customUserDetailsService, CookieUtil cookieUtil) {
        this.jwtProvider = jwtProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.cookieUtil = cookieUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();

        // 인증이 필요 없는 경로는 필터 건너뜀
        if (uri.startsWith("/api/auth/verify") || uri.startsWith("/api/auth/join")
                || uri.startsWith("/api/auth/send-verification-email")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 가져오고 없으면 null
//        String accessToken = cookieUtil.getCookie(request, "accessToken")
//                .map(Cookie::getValue)
//                .orElse(null);
        String jwtToken = request.getHeader("Authorization");
        String accessToken = null;

        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            accessToken = jwtToken.substring(7); // "Bearer " 제거
        }


        // validateToken으로 토큰 유효성 검사
        // 토큰이 존재하고, 유효하다면
        if (accessToken != null && jwtProvider.validateToken(accessToken)) {
            // 토큰에서 loginId를 추출
            String loginId = jwtProvider.getLoginId(accessToken);

            // loginId로 CustomUserDetailsService를 통해 DB에서 사용자 정보 조회
            CustomUserPrincipal userDetails = (CustomUserPrincipal) customUserDetailsService.loadUserByUsername(loginId);

            if (userDetails != null) {
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // SecurityContext에 Authentication 객체를 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), request.getRequestURI());
            }
        }

        // 다음 필터로 요청을 전달
        filterChain.doFilter(request, response);
    }
}
