package com.gatewayservice.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {
    private static final String BEARER_PREFIX = "Bearer ";
    private final SecretKey key;

    public JwtFilter(@Value("${jwt.secret.key}") String jwtSecret) {
        super(Config.class);
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // 인증이 필요없는 URL은 패스
            if (isPermitAllUrl(request.getURI().getPath())) {
                return chain.filter(exchange);
            }

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return handleUnauthorized(response, "Authorization header is missing");
            }

            String authHeader = request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).getFirst();
            if (!authHeader.startsWith(BEARER_PREFIX)) {
                return handleUnauthorized(response, "Invalid Authorization header format");
            }

            String token = authHeader.substring(BEARER_PREFIX.length()).trim();

            try {
                Claims claims = validateAndParseToken(token);

                // 토큰 타입 검증
                String tokenType = claims.get("type", String.class);
                if (!"accessToken".equals(tokenType)) {
                    return handleUnauthorized(response, "Invalid token type");
                }

                // 만료 시간 검증
                if (claims.getExpiration().before(new Date())) {
                    return handleUnauthorized(response, "Token has expired");
                }

                // 사용자 정보를 요청 헤더에 추가
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Email", claims.get("email", String.class))
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                log.error("JWT validation failed", e);
                return handleUnauthorized(response, "JWT validation failed: " + e.getMessage());
            }
        };
    }

    private Claims validateAndParseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isPermitAllUrl(String path) {
        return path.startsWith("/api/v1/member/login") ||
                path.startsWith("/api/v1/member/register") ||
                path.startsWith("/api/v1/member/refresh");
    }

    private Mono<Void> handleUnauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        String body = String.format("{\"error\": \"%s\", \"message\": \"%s\"}",
                HttpStatus.UNAUTHORIZED.getReasonPhrase(), message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    @Data
    public static class Config {
        private boolean preLogger;
        private boolean postLogger;
    }
}