package com.concert.app.infrastructure.repository.emailSender;

import com.concert.app.domain.emailSender.EmailSenderRepository;
import com.concert.app.domain.emailSender.dto.EmailSenderToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.HashMap;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EmailSenderRepositoryImpl implements EmailSenderRepository {
    private static final String REDIS_KEY_PREFIX = "email_verification:";
    private static final Duration TOKEN_VALIDITY = Duration.ofMinutes(5);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void save(EmailSenderToken emailSenderToken, String token) throws JsonProcessingException {
        // 토큰 생성
        String redisKey = REDIS_KEY_PREFIX + token;
        redisTemplate.opsForValue().set(redisKey, objectMapper.writeValueAsString(emailSenderToken), TOKEN_VALIDITY); // 토큰 만료 시간 설정
    }

    @Override
    public Boolean verifyToken(String token) {
        return redisTemplate.hasKey(REDIS_KEY_PREFIX + token);
    }

    @Override
    public HashMap<String, String> getToken(String token) throws JsonProcessingException {
        String tokenString = redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + token);
        return objectMapper.readValue(tokenString, new TypeReference<>() {});
    }

    @Override
    public void updateToken(String token, HashMap<String, String> tokenData) throws JsonProcessingException {
        redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + token, objectMapper.writeValueAsString(tokenData), TOKEN_VALIDITY);
    }
}
