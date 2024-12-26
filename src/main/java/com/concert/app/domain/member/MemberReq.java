package com.concert.app.domain.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberReq(
        String uuid,    // 이메일 인증 토큰으로 사용
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank String name,
        @NotBlank String phoneNumber,
        @NotBlank String address1,
        @NotBlank String address2,
        boolean isAdmin,
        String adminToken
) {}
