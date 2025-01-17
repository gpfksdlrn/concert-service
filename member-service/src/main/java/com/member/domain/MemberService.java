package com.member.domain;

import com.common.exception.ApiException;
import com.common.exception.ExceptionCode;
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
    public void loginMember(String email, String password, HttpServletResponse res) {
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

        // Access Token은 헤더로 전달
        res.setHeader("Authorization", "Bearer " + accessToken);

        // Refresh Token은 HttpOnly 쿠키로 저장
        setRefreshTokenCookie(res, refreshToken);

        // Redis에 Refresh Token 저장
        refreshTokenRepository.storeRefreshToken(member.getEmail(), refreshToken);
    }

    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true); // JavaScript 접근 금지
        // refreshTokenCookie.setSecure(true); // HTTPS에서만 사용
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
        invalidateRefreshTokenCookie(res, refreshToken);

        return "로그아웃 완료";
    }

    private void invalidateRefreshTokenCookie(HttpServletResponse res, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setDomain("localhost"); // 도메인 설정
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 만료시간 7일
        // 필요하다면 secure 옵션도 적용
        // cookie.setSecure(true);
        res.addCookie(cookie);
    }

    public SelectMemberInfoResult selectMemberInfo(String token) {
        // 토큰에서 이메일 추출
        String encodeEmail = jwtProvider.getEmailFromToken(token);
        String email = encryptionUtil.decrypt(encodeEmail);
        Member member = memberRepository.findByEmail(encodeEmail);

        return new SelectMemberInfoResult(
                email,
                encryptionUtil.decrypt(member.getName()),
                encryptionUtil.decrypt(member.getPhoneNumber()),
                encryptionUtil.decrypt(member.getAddress1()),
                encryptionUtil.decrypt(member.getAddress2()),
                member.getBalance(),
                member.getRole() == Member.UserRoleEnum.ADMIN
        );
    }

    public void refreshAccessToken(String refreshToken, HttpServletResponse res) {
        if(refreshToken == null) {
            throw new ApiException(ExceptionCode.REFRESH_TOKEN_NOT_FOUND, LogLevel.ERROR);
        }

        // Refresh Token 검증 및 새로운 Access Token 발급
        String newAccessToken = jwtProvider.refreshAccessToken(refreshToken);

        // Access Token 헤더에 설정
        res.setHeader("Authorization", "Bearer " + newAccessToken);
    }

    @Transactional
    public ChargeMemberPointResult chargeMemberPoint(String token, Long point) {
        // 토큰에서 이메일 추출
        String encodeEmail = jwtProvider.getEmailFromToken(token);
        Member member = memberRepository.findByEmail(encodeEmail);

        // 최대 충전 금액 체크 (10만원)
        if(member.getBalance() + point > 100000) {
            throw new ApiException(ExceptionCode.MAX_CHARGE_POINT, LogLevel.ERROR);
        }

        // 최소 충전 금액 체크 (10원)
        if(point < 10) {
            throw new ApiException(ExceptionCode.MIN_CHARGE_POINT, LogLevel.ERROR);
        }

        // 포인트 충전
        member.chargePoint(point);

        return new ChargeMemberPointResult(member.getId(), point, member.getBalance());
    }
}