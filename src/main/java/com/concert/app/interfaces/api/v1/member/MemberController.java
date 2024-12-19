package com.concert.app.interfaces.api.v1.member;

import com.concert.app.domain.member.MemberService;
import com.concert.app.interfaces.api.v1.member.res.MemberRes;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원 서비스 API", description = "회원 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/api/member/{id}")
    public MemberRes getMemberById(@PathVariable Long id) {
        return memberService.getMemberById(id);
    }
}