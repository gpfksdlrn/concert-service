package com.concert.app.interfaces.api.v1.auth;

import com.concert.app.domain.mailSender.EmailSendService;
import com.concert.app.interfaces.api.common.CommonRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "이메일 인증 API", description = "이메일 인증 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class EmailAuthController {

    private final EmailSendService emailSendService;

    // 인증 요청
    @PostMapping("/email-verification")
    public CommonRes<String> requestEmailAuth(@RequestParam String email) throws Exception {
        return emailSendService.sendEmail(email);
    }

    // 검증
    @GetMapping("/email-verification/{token}")
    public CommonRes<String> verifyEmail(@PathVariable String token) throws JsonProcessingException {
        return emailSendService.verifyToken(token);
    }
}