package com.member.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public interface MemberRepository {
    void existsByEmail(@Email @NotBlank String email);
    void save(Member member);
    Member findByEmail(String encodingEmail);
}