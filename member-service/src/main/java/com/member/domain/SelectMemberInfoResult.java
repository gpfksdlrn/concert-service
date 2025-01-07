package com.member.domain;

public record SelectMemberInfoResult(
        String email,
        String name,
        String phoneNumber,
        String address1,
        String address2,
        Long balance,
        boolean isAdmin
) {
}