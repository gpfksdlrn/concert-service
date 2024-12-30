package com.concert.app.domain.emailSender;

public interface EmailSender {
    void sendMail(String to, String subject, String body) throws Exception;
}