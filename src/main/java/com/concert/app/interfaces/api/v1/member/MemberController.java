package com.concert.app.interfaces.api.v1.member;

import com.concert.app.domain.member.LoginTokenRes;
import com.concert.app.domain.member.MemberReq;
import com.concert.app.domain.member.MemberService;
import com.concert.app.domain.member.SelectMemberResult;
import com.concert.app.interfaces.api.common.CommonRes;
import com.concert.app.interfaces.api.v1.member.req.MemberLogOut;
import com.concert.app.interfaces.api.v1.member.req.MemberLogin;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원 서비스 API", description = "회원 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/register")
    public CommonRes<SelectMemberResult> registerMember(@RequestBody @Valid MemberReq req) throws Exception {
        return CommonRes.success(memberService.registerMember(req));
    }

    @PostMapping("/login")
    public CommonRes<LoginTokenRes> loginMember(@RequestBody MemberLogin req, HttpServletResponse res) {
        return CommonRes.success(memberService.loginMember(req.email(), req.password(), res));
    }

    @PostMapping("/logout")
    public CommonRes<String> logout(@RequestBody MemberLogOut req, HttpServletResponse res) {
        return CommonRes.success(memberService.logoutMember(req.email(), res));
    }
}