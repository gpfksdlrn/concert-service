package com.concert.app.domain.member;

public record LoginTokenRes(
        String accessToken,
        String refreshToken
) {
}