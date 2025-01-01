package com.concert.app.interfaces.api.filter;

import com.concert.app.domain.auth.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 헤더에서 AccessToken 추출
        String authHeader = request.getHeader(AUTH_HEADER);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            log.error("올바르지 않은 Authorization 헤더: {}", authHeader);
            throw new AuthenticationException("헤더에 Authorization 가 없습니다.") {
            };
        }

        String accessToken = authHeader.substring(BEARER_PREFIX.length());

        // AccessToken 검증
        if (!jwtProvider.validateToken(accessToken)) {
            log.error("유효하지 않은 AccessToken: {}", accessToken);

            // RefreshToken 검증 및 AccessToken 재발급
            String refreshToken = getRefreshTokenFromCookie(request);
            if (refreshToken == null) {
                log.error("RefreshToken 이 쿠키에 존재하지 않습니다.");
                throw new AuthenticationException("RefreshToken 이 쿠키에 존재하지 않습니다.") {};
            }

            try {
                String newAccessToken = jwtProvider.refreshAccessToken(refreshToken);
                response.setHeader(AUTH_HEADER, BEARER_PREFIX + newAccessToken);
                setAuthentication(newAccessToken);
                log.info("새로운 AccessToken 발급 완료: {}", newAccessToken);
            } catch (Exception e) {
                log.error("AccessToken 재발급 실패: {}", e.getMessage());
                throw e;
            }

        } else {
            // AccessToken 유효 시 인증 정보 설정
            setAuthentication(accessToken);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 인증 정보 설정
     */
    private void setAuthentication(String token) {
        // 토큰에서 email 추출
        String email = jwtProvider.getEmailFromToken(token);
        // 인증 객체 생성 -> SecurityContextHolder 에 저장
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
        // SecurityContext 에 인증 정보 등록
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    /**
     * 쿠키에서 RefreshToken 가져오기
     * @param request HttpServletRequest
     * @return RefreshToken 값
     */
    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}