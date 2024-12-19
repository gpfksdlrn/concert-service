package com.concert.app.interfaces.api.v1.member.res;

public record MemberRes(
        Long id,
        String email,
        String name,
        String phoneNumber,
        String address1,
        String address2
) {
}