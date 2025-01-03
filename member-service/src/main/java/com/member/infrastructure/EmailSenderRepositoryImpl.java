package com.member.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.domain.EmailSenderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
@RequiredArgsConstructor
public class EmailSenderRepositoryImpl implements EmailSenderRepository {
    private static final String REDIS_KEY_PREFIX = "email_verification:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public HashMap<String, String> getToken(String token) throws JsonProcessingException {
        String tokenString = redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + token);
        return objectMapper.readValue(tokenString, new TypeReference<>() {});
    }
}
