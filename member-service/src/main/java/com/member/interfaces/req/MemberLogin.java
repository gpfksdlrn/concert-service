package com.member.interfaces.req;

public record MemberLogin(
        String email,
        String password
) {
}