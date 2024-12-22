package com.concert.app.domain.mailSender;

import com.concert.app.interfaces.api.common.CommonRes;
import com.concert.app.interfaces.api.exception.ApiException;
import com.concert.app.interfaces.api.exception.ExceptionCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailSendService {
    private final StringRedisTemplate redisTemplate;
    private final EmailSender emailSender;
    private final ObjectMapper objectMapper;

    private static final String REDIS_KEY_PREFIX = "email_verification:";
    private static final String BASE_URL = "http://localhost:8080/api/v1/auth/email-verification/";
    private static final Duration TOKEN_VALIDITY = Duration.ofMinutes(5);

    public CommonRes<String> sendEmail(@Valid @Email String email) throws Exception {
        // 토큰 생성
        String token = UUID.randomUUID().toString();
        String redisKey = REDIS_KEY_PREFIX + token;
        log.info("[TOKEN 생성] token={}, email={}", token, email);

        // Redis 데이터 저장
        Map<String, String> tokenData = new HashMap<>();
        tokenData.put("email", email);
        tokenData.put("expired", "false");
        tokenData.put("create_at", String.valueOf(System.currentTimeMillis()));

        redisTemplate.opsForValue().set(redisKey, objectMapper.writeValueAsString(tokenData), TOKEN_VALIDITY); // 토큰 만료 시간 설정

        // 이메일 전송
        String link = BASE_URL + token;
        String emailContent = "아래 버튼을 클릭해주세요.<br>" +
                "<a href=\"" + link + "\" style=\"background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block;\">이메일 인증</a>";
        emailSender.sendMail(email, "메일 인증 요청", emailContent);

        log.info(link);
        return CommonRes.success("이메일 인증 메일이 발송되었습니다.");
    }

    public CommonRes<String> verifyToken(String token) throws JsonProcessingException {
        String redisKey = REDIS_KEY_PREFIX + token;

        if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
            log.error("[TOKEN 검증 실패] 토큰이 존재하지 않습니다. token={}", token);
            throw new ApiException(ExceptionCode.TOKEN_INVALID, LogLevel.ERROR);
        }

        String json = redisTemplate.opsForValue().get(redisKey);
        HashMap<String, String> tokenData = objectMapper.readValue(json, new TypeReference<>() {});

        if ("true".equals(tokenData.get("expired"))) {
            log.error("[TOKEN 검증 실패] 토큰 만료되었습니다. token={}", token);
            throw new ApiException(ExceptionCode.TOKEN_EXPIRED, LogLevel.ERROR);
        }

        // 토큰 상태 업데이트
        tokenData.put("expired", "true");
        redisTemplate.opsForValue().set(redisKey, objectMapper.writeValueAsString(tokenData), TOKEN_VALIDITY);

        return CommonRes.success("이메일 인증 성공");
    }
}
