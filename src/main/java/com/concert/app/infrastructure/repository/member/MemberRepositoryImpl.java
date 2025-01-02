package com.concert.app.infrastructure.repository.member;

import com.common.exception.ApiException;
import com.common.exception.ExceptionCode;
import com.concert.app.domain.member.Member;
import com.concert.app.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberJpaRepository jpaRepository;

    @Override
    public void existsByEmail(String email) {
        if(jpaRepository.existsByEmail(email))
            throw new ApiException(ExceptionCode.DUPLICATED_EMAIL, LogLevel.ERROR);
    }

    @Override
    public void save(Member member) {
        jpaRepository.save(member);
    }

    @Override
    public Member findByEmail(String encodingEmail) {
        return jpaRepository.findByEmail(encodingEmail)
                .orElseThrow(() -> new ApiException(ExceptionCode.E404, LogLevel.ERROR));

    }
}