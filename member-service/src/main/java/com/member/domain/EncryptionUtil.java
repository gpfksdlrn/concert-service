package com.member.domain;

public interface EncryptionUtil {
    String encrypt(String data);
    String decrypt(String data);
}