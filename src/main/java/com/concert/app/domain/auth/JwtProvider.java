package com.concert.app.domain.auth;

public interface JwtProvider {
    String generateToken(String subject);
    boolean validateToken(String token);
}
