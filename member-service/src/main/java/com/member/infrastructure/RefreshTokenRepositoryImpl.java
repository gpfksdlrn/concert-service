package com.member.infrastructure;

import com.member.domain.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private final StringRedisTemplate redisTemplate;
    private static final String REDIS_KEY_REFRESH = "refresh_token:";
    private static final int REFRESH_TOKEN_EXPIRES_IN_DAYS = 7;

    @Override
    public void storeRefreshToken(String email, String refreshToken) {
        redisTemplate.opsForValue().set(REDIS_KEY_REFRESH + email, refreshToken, REFRESH_TOKEN_EXPIRES_IN_DAYS, TimeUnit.DAYS);
    }

    @Override
    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue().get(REDIS_KEY_REFRESH + email);
    }

    public void deleteRefreshToken(String email) {
        redisTemplate.delete(REDIS_KEY_REFRESH + email);
    }
}