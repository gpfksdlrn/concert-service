package com.concert.app.domain.member;

import java.time.LocalDateTime;

public record SelectMemberResult(
        String email,
        String name,
        boolean isAdmin,
        LocalDateTime createdAt
) {}