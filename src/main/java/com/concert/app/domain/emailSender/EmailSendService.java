package com.concert.app.domain.emailSender;

import com.concert.app.domain.emailSender.dto.EmailSenderToken;
import com.concert.app.domain.exception.ApiException;
import com.concert.app.domain.exception.ExceptionCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailSendService {
    private final EmailSender emailSender;
    private final EmailSenderRepository emailSenderRepository;

    @Value("${spring.mail.url}")
    private String url;

    @Transactional
    public String sendEmail(@Valid @Email String email) throws Exception {
        // 토큰 생성
        String token = UUID.randomUUID().toString();
        email = email.trim();

        // Redis 데이터 저장
        EmailSenderToken emailSenderToken = new EmailSenderToken(email, String.valueOf(EmailCheckEnum.FALSE), String.valueOf(System.currentTimeMillis()));
        emailSenderRepository.save(emailSenderToken, token);

        // 이메일 전송
        String link = url + token;
        String emailContent = "아래 버튼을 클릭해주세요.<br>" +
                "<a href=\"" + link + "\" style=\"background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block;\">이메일 인증</a>";
        emailSender.sendMail(email, "메일 인증 요청", emailContent);

        log.info(link);
        return "이메일 인증 메일이 발송되었습니다.";
    }

    @Transactional
    public String verifyToken(String token) throws JsonProcessingException {
        if (Boolean.FALSE.equals(emailSenderRepository.verifyToken(token))) {
            log.error("[TOKEN 검증 실패] 토큰이 존재하지 않습니다. token={}", token);
            throw new ApiException(ExceptionCode.TOKEN_INVALID, LogLevel.ERROR);
        }

        HashMap<String, String> tokenData = emailSenderRepository.getToken(token);

        if ("true".equals(tokenData.get("expired"))) {
            log.error("[TOKEN 검증 실패] 토큰 만료되었습니다. token={}", token);
            throw new ApiException(ExceptionCode.TOKEN_EXPIRED, LogLevel.ERROR);
        }

        // 토큰 상태 업데이트(검증 끝났으니 만료시킴 - 두 번 못 쓰게)
        tokenData.put("emailCheck", String.valueOf(EmailCheckEnum.TRUE));
        emailSenderRepository.updateToken(token, tokenData);

        return "이메일 인증 성공";
    }
}
