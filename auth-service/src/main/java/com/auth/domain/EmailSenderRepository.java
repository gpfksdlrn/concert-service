package com.auth.domain;

import com.auth.domain.dto.EmailSenderToken;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.HashMap;

public interface EmailSenderRepository {
    void save(EmailSenderToken emailSenderToken, String token) throws JsonProcessingException;

    Boolean verifyToken(String token);

    HashMap<String, String> getToken(String token) throws JsonProcessingException;

    void updateToken(String token, HashMap<String, String> tokenData) throws JsonProcessingException;
}