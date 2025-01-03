package com.member.interceptor;

import com.member.domain.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.naming.AuthenticationException;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws AuthenticationException {
        // Authorization 헤더 추출
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        authorizationHeader = authorizationHeader.substring(BEARER_PREFIX.length());
        if (StringUtils.hasText(authorizationHeader) || authorizationHeader.startsWith(BEARER_PREFIX)) {
            if(jwtProvider.validateToken(authorizationHeader)) {
                return true;
            } else {
                log.error("유효하지 않은 토큰입니다. [토큰: {}]", authorizationHeader);
                throw new AuthenticationException("권한에러 발생");
            }
        } else {
            log.error("Authorization 헤더가 없거나 형식이 잘못되었습니다.");
            throw new AuthenticationException("권한에러 발생");
        }
    }
}
