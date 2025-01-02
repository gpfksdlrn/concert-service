package com.concert.app.infrastructure.auth;

import com.common.exception.ApiException;
import com.common.exception.ExceptionCode;
import com.concert.app.domain.auth.JwtProvider;
import com.concert.app.domain.auth.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProviderImpl implements JwtProvider {

    private static Key key; // JWT 서명 키를 위한 정적 변수
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.secret-key}")
    private String secretKeyString; // JWT 키를 가져오는 필드

    @Value("${jwt.access-token-expiration:1800000}") // AccessToken 만료 시간 (기본값: 30분)
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:604800000}") // RefreshToken 만료 시간 (기본값: 7일)
    private long refreshTokenExpiration;

    @PostConstruct
    public void initKey() {
        key = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    /**
     * AccessToken 생성
     * @param email 사용자 이메일
     * @return 생성된 AccessToken
     */
    @Override
    public String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email) // email을 subject로 설정
                .setIssuedAt(new Date()) // 토큰 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration)) // 만료 시간
                .claim("type", "accessToken") // 토큰 타입
                .signWith(key, SignatureAlgorithm.HS256) // 서명
                .compact();
    }

    /**
     * RefreshToken 생성
     * @param email 사용자 이메일
     * @return 생성된 RefreshToken
     */
    @Override
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email) // email을 subject로 설정
                .setIssuedAt(new Date()) // 토큰 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration)) // 만료 시간
                .claim("type", "refreshToken") // 토큰 타입
                .signWith(key, SignatureAlgorithm.HS256) // 서명
                .compact();
    }

    /**
     * 토큰 유효성 검증
     * @param token 클라이언트에서 전달받은 JWT 토큰
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    @Override
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

    /**
     * 토큰에서 이메일 추출
     * @param token JWT 토큰
     * @return 이메일
     */
    @Override
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * RefreshToken 유효성 검증 및 AccessToken 갱신
     * @param refreshToken 유효한 RefreshToken
     * @return 새로운 AccessToken
     */
    @Override
    public String refreshAccessToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new ApiException(ExceptionCode.REFRESH_TOKEN_INVALID, LogLevel.ERROR);
        }

        String email = getEmailFromToken(refreshToken);

        // Redis 에서 RefreshToken 검증
        String storedRefreshToken = refreshTokenRepository.getRefreshToken(email);
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new ApiException(ExceptionCode.REFRESH_TOKEN_MISMATCH, LogLevel.ERROR);
        }

        return generateAccessToken(email);
    }
}