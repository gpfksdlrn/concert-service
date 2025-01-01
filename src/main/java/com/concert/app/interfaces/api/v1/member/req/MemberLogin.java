package com.concert.app.interfaces.api.v1.member.req;

public record MemberLogin(
        String email,
        String password
) {
}