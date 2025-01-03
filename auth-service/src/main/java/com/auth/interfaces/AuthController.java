package com.auth.interfaces;

import com.auth.domain.EmailSendService;
import com.common.res.CommonRes;
import com.auth.interfaces.req.EmailSenderReq;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "이메일 인증 API", description = "이메일 인증 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final EmailSendService emailSendService;

    // 인증 요청
    @PostMapping("/email-verification")
    public CommonRes<String> requestEmailAuth(@RequestBody EmailSenderReq req) throws Exception {
        return CommonRes.success(emailSendService.sendEmail(req.email()));
    }

    // 검증
    @GetMapping("/email-verification/{token}")
    public CommonRes<String> verifyEmail(@PathVariable String token) throws JsonProcessingException {
        return CommonRes.success(emailSendService.verifyToken(token));
    }
}