package com.concert.app.domain.encryption;

public interface EncryptionUtil {
    String encrypt(String data);
    String decrypt(String data);
}