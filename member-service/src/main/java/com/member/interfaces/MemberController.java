package com.member.interfaces;

import com.common.res.CommonRes;
import com.member.domain.*;
import com.member.interfaces.req.ChargeMemberPoint;
import com.member.interfaces.req.MemberLogOut;
import com.member.interfaces.req.MemberLogin;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

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
    public CommonRes<Object> loginMember(@RequestBody MemberLogin req, HttpServletResponse res) {
        memberService.loginMember(req.email(), req.password(), res);
        return CommonRes.success(new HashMap<>());
    }

    @PostMapping("/logout")
    public CommonRes<String> logout(@RequestBody MemberLogOut req, HttpServletResponse res) {
        return CommonRes.success(memberService.logoutMember(req.email(), res));
    }

    @GetMapping("/info")
    public CommonRes<SelectMemberInfoResult> selectMemberInfo(@RequestHeader("Authorization") String token) {
        return CommonRes.success(memberService.selectMemberInfo(token));
    }

    @PostMapping("/refresh")
    public CommonRes<Object> refreshAccessToken(@CookieValue("refreshToken") String refreshToken, HttpServletResponse res) {
        memberService.refreshAccessToken(refreshToken, res);
        return CommonRes.success(new HashMap<>());
    }

    @PostMapping("/point/charge")
    public CommonRes<ChargeMemberPointResult> chargeMemberPoint(@RequestHeader("Authorization") String token, @RequestBody ChargeMemberPoint req) {
        return CommonRes.success(memberService.chargeMemberPoint(token, req.point()));
    }
}