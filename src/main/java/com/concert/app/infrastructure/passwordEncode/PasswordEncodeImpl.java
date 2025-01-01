package com.concert.app.infrastructure.passwordEncode;

import com.concert.app.domain.member.PasswordEncode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordEncodeImpl implements PasswordEncode {
    private final PasswordEncoder passwordEncoder;

    @Override
    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean matches(String inputPassword, String userPassword) {
        return passwordEncoder.matches(inputPassword, userPassword);
    }
}