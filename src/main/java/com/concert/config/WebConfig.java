package com.concert.config;

import com.concert.app.interfaces.api.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**") // 인증이 필요한 경로
                .excludePathPatterns(
                        "/api/v1/auth/**", // 로그인, 회원가입, 이메일 인증 등 인증이 필요 없는 경로
                        "/api/v1/concerts/list", // 콘서트 조회
                        "/swagger-ui/**", "/v3/api-docs/**" // Swagger 관련 경로
                );
    }
}
