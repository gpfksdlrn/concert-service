package com.member.domain;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.HashMap;

public interface EmailSenderRepository {
    HashMap<String, String> getToken(String uuid) throws JsonProcessingException;
}
