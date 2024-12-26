package com.concert.app.domain.member;

import com.concert.app.domain.encryption.EncryptionService;
import com.concert.app.interfaces.api.exception.ApiException;
import com.concert.app.interfaces.api.exception.ExceptionCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final EncryptionService encryptionService;
    private final StringRedisTemplate redisTemplate;

    private @Value("${jwt.admin-token}") String ADMIN_TOKEN;
    private static final String REDIS_KEY_PREFIX = "email_verification:";

    public SelectMemberResult registerMember(@Valid MemberReq memberReq) {

        // 0. Email 형식 검사 -> @Email (@Valid 에 의해 자동 검사)
        // 1. 이메일 중복 검사
        String encryptedEmail = encryptionService.encrypt(memberReq.email());
        memberRepository.existsByEmail(encryptedEmail);

        // 2. Redis 에서 이메일 인증 상태 확인
        String redisKey = REDIS_KEY_PREFIX + memberReq.uuid();
        String redisValue = redisTemplate.opsForValue().get(redisKey);
        if (redisValue == null) {
            throw new ApiException(ExceptionCode.TOKEN_INVALID, LogLevel.ERROR);
        }

        // 관리자 여부
        Member.UserRoleEnum role = Member.UserRoleEnum.USER;
        if (memberReq.isAdmin()) {
            if (!ADMIN_TOKEN.equals(memberReq.adminToken())) {
                throw new ApiException(ExceptionCode.WRONG_ADMIN_TOKEN, LogLevel.ERROR);
            }
            role = Member.UserRoleEnum.ADMIN;
        }

        Member member = new Member(
                null,
                encryptedEmail,
                encryptionService.encrypt(memberReq.password()),
                encryptionService.encrypt(memberReq.name()),
                encryptionService.encrypt(memberReq.phoneNumber()),
                encryptionService.encrypt(memberReq.address1()),
                encryptionService.encrypt(memberReq.address2()),
                0L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                false,
                role
        );

        // DB에 저장
        memberRepository.save(member);
        return new SelectMemberResult(
                encryptionService.decrypt(member.getEmail()),
                encryptionService.decrypt(member.getName()),
                member.getRole() == Member.UserRoleEnum.ADMIN,
                member.getCreatedAt()
        );
    }
}