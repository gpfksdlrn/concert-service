package com.auth.domain;

public interface EmailSender {
    void sendMail(String to, String subject, String body) throws Exception;
}