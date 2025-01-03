package com.member.interfaces;

import com.common.res.CommonRes;
import com.member.domain.LoginTokenResult;
import com.member.domain.MemberReq;
import com.member.domain.MemberService;
import com.member.domain.SelectMemberResult;
import com.member.interfaces.req.MemberLogOut;
import com.member.interfaces.req.MemberLogin;
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
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/register")
    public CommonRes<SelectMemberResult> registerMember(@RequestBody @Valid MemberReq req) throws Exception {
        return CommonRes.success(memberService.registerMember(req));
    }

    @PostMapping("/login")
    public CommonRes<LoginTokenResult> loginMember(@RequestBody MemberLogin req, HttpServletResponse res) {
        return CommonRes.success(memberService.loginMember(req.email(), req.password(), res));
    }

    @PostMapping("/logout")
    public CommonRes<String> logout(@RequestBody MemberLogOut req, HttpServletResponse res) {
        return CommonRes.success(memberService.logoutMember(req.email(), res));
    }
}