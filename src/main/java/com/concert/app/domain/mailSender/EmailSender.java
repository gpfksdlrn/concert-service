package com.concert.app.domain.mailSender;

public interface EmailSender {
    void sendMail(String to, String subject, String body) throws Exception;
}