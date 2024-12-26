package com.concert.app.domain.encryption;

public interface EncryptionService {
    String encrypt(String data);
    String decrypt(String data);
}