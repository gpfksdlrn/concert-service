package com.auth.domain.dto;

public record EmailSenderToken(
        String email,
        String emailCheck,
        String createdAt
) {
}