package com.concert.app.domain.auth;

public interface JwtProvider {
    /**
     * AccessToken 생성
     * @param email 사용자 이메일
     * @return 생성된 AccessToken
     */
    String generateAccessToken(String email);

    /**
     * RefreshToken 생성
     * @param email 사용자 이메일
     * @return 생성된 RefreshToken
     */
    String generateRefreshToken(String email);

    /**
     * 토큰 유효성 검증
     * @param token 클라이언트에서 전달받은 JWT 토큰
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    boolean validateToken(String token);

    /**
     * 토큰에서 이메일 추출
     * @param token JWT 토큰
     * @return 이메일
     */
    String getEmailFromToken(String token);

    /**
     * RefreshToken 유효성 검증 및 AccessToken 갱신
     * @param refreshToken 클라이언트에서 전달받은 RefreshToken
     * @return 새로운 AccessToken
     */
    String refreshAccessToken(String refreshToken);
}