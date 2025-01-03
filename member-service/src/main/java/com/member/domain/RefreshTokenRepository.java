package com.member.domain;

public interface RefreshTokenRepository {
    void storeRefreshToken(String email, String refreshToken);

    String getRefreshToken(String email);

    void deleteRefreshToken(String email);
}