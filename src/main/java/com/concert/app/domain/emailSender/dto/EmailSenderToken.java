package com.concert.app.domain.emailSender.dto;

public record EmailSenderToken(
        String email,
        String emailCheck,
        String createdAt
) {
}