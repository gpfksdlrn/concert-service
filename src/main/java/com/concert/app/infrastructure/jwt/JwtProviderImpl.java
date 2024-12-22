package com.concert.app.infrastructure.jwt;

import com.concert.app.domain.auth.JwtProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtProviderImpl implements JwtProvider {

    private static Key key; // JWT 서명 키를 위한 정적 변수

    @Value("${jwt.secret-key}")
    private String secretKeyString; // JWT 키를 가져오는 필드

    @PostConstruct
    public void initKey() {
        key = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    @Override
    public String generateToken(String email) {
        return Jwts.builder()
            .claim("email", email)
            .claim("token", UUID.randomUUID().toString())
            .claim("enteredAt", new Date())
            .claim("expiredAt", new Date(System.currentTimeMillis() + 300000)) // 5분 후 만료
            .signWith(key, SignatureAlgorithm.HS256) // 서명 포함
            .compact();
    }

    // 토큰의 서명을 검증하고, 만료 여부를 확인
    // JWT 토큰에서 userId 추출
    public boolean validateToken(String token) {
        try {
            // 토큰 파싱 (이 과정에서 서명 및 만료 등 검증이 일어남)
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true; // 예외 없이 파싱 성공 시 유효한 토큰
        } catch (Exception e) {
            return false; // 파싱 중 예외 발생 시 유효하지 않은 토큰
        }
    }
}