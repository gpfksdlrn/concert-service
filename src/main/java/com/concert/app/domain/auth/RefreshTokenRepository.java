package com.concert.app.domain.auth;

public interface RefreshTokenRepository {
    void storeRefreshToken(String email, String refreshToken);

    String getRefreshToken(String email);

    void deleteRefreshToken(String email);
}