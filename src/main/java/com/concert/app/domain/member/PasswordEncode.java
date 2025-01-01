package com.concert.app.domain.member;

public interface PasswordEncode {
    String encode(String password);
    boolean matches(String inputPassword, String userPassword);
}