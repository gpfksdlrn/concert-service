package com.member.domain;

public record LoginTokenResult(
        String accessToken,
        String refreshToken
) {
}