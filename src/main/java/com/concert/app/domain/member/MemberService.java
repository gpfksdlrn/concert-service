package com.concert.app.domain.member;

import com.concert.app.interfaces.api.v1.member.res.MemberRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class MemberService {
    public MemberRes getMemberById(Long id) {
        return new MemberRes(
                id,
                "test@gmail.com",
                "아무개",
                "01012341234",
                "서울특별시 관악구",
                "아파트 123"
        );
    }
}