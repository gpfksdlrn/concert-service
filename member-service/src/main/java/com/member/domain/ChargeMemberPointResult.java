package com.member.domain;

public record ChargeMemberPointResult(
        Long userId,
        Long chargePoint,
        Long balance
) {
}