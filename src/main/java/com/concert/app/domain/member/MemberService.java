package com.concert.app.domain.member;

import com.common.exception.ApiException;
import com.common.exception.ExceptionCode;
import com.concert.app.domain.auth.JwtProvider;
import com.concert.app.domain.auth.RefreshTokenRepository;
import com.concert.app.domain.emailSender.EmailCheckEnum;
import com.concert.app.domain.emailSender.EmailSenderRepository;
import com.concert.app.domain.encryption.EncryptionUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final EncryptionUtil encryptionUtil;
    private final EmailSenderRepository emailSenderRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncode passwordEncode;
    private final JwtProvider jwtProvider;

    @Value("${jwt.admin-token}")
    private String adminToken;

    @Transactional
    public SelectMemberResult registerMember(@Valid MemberReq memberReq) throws Exception {
        // Redis 에서 이메일 확인
        HashMap<String, String> emailToken = emailSenderRepository.getToken(memberReq.uuid());
        String emailCheck = emailToken.get("emailCheck");
        String email = emailToken.get("email");

        // 메일이 인증 된 메일인지 확인
        if(!emailCheck.equals(String.valueOf(EmailCheckEnum.TRUE))) {
            throw new ApiException(ExceptionCode.EMAIL_NOT_CHECK, LogLevel.ERROR);
        }

        // 인증한 메일과 가입시도하는 메일이 일치하는지 확인
        if(!email.equals(memberReq.email())) {
            throw new ApiException(ExceptionCode.EMAIL_MISMATCH, LogLevel.ERROR);
        }

        // 이메일 중복 검사
        String encryptedEmail = encryptionUtil.encrypt(memberReq.email());
        memberRepository.existsByEmail(encryptedEmail);

        // 관리자 여부
        Member.UserRoleEnum role = Member.UserRoleEnum.USER;
        if (memberReq.isAdmin()) {
            if (!adminToken.equals(memberReq.adminToken())) {
                throw new ApiException(ExceptionCode.WRONG_ADMIN_TOKEN, LogLevel.ERROR);
            }
            role = Member.UserRoleEnum.ADMIN;
        }

        Member member = new Member(
                null,
                encryptedEmail,
                passwordEncode.encode(memberReq.password()),
                encryptionUtil.encrypt(memberReq.name()),
                encryptionUtil.encrypt(memberReq.phoneNumber()),
                encryptionUtil.encrypt(memberReq.address1()),
                encryptionUtil.encrypt(memberReq.address2()),
                0L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                false,
                role
        );

        // DB에 저장
        memberRepository.save(member);
        return new SelectMemberResult(
                encryptionUtil.decrypt(member.getEmail()),
                encryptionUtil.decrypt(member.getName()),
                member.getRole() == Member.UserRoleEnum.ADMIN,
                member.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public LoginTokenRes loginMember(String email, String password, HttpServletResponse res) {
        // 이메일 암호화
        String encodingEmail = encryptionUtil.encrypt(email);

        // 이메일로 회원 정보 조회
        Member member = memberRepository.findByEmail(encodingEmail);

        // 비밀번호 검증
        if (!passwordEncode.matches(password, member.getPassword())) {
            throw new ApiException(ExceptionCode.WRONG_PASSWORD, LogLevel.ERROR);
        }

        // JWT 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(member.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(member.getEmail());

        // 클라이언트가 Authorization 헤더로 AccessToken을 사용할 수 있도록 설정
        res.setHeader("Authorization", "Bearer " + accessToken);

        // RefreshToken 을 HttpOnly 쿠키에 저장
        setRefreshTokenCookie(res, refreshToken);
        // Redis 에 RefreshToken 저장
        refreshTokenRepository.storeRefreshToken(email, refreshToken);

        return new LoginTokenRes(accessToken, refreshToken);
    }

    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true); // JavaScript 접근 금지
        refreshTokenCookie.setSecure(true); // HTTPS에서만 사용
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(refreshTokenCookie);
    }

    public String logoutMember(String email, HttpServletResponse res) {
        String refreshToken = refreshTokenRepository.getRefreshToken(email);

        // 리프레시 토큰이 존재 할 경우 리프레시 토큰 삭제
        if(StringUtils.hasText(refreshToken)) {
            refreshTokenRepository.deleteRefreshToken(email);
        }

        // 쿠키 만료
        invalidateRefreshTokenCookie(res);

        return "로그아웃 완료";
    }

    private void invalidateRefreshTokenCookie(HttpServletResponse res) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 만료 시간 0으로 설정 → 즉시 만료
        // 필요하다면 secure 옵션도 적용
        // cookie.setSecure(true);
        res.addCookie(cookie);
    }
}