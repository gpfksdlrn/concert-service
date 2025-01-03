package com.member.domain;

public interface PasswordEncode {
    String encode(String password);
    boolean matches(String inputPassword, String userPassword);
}