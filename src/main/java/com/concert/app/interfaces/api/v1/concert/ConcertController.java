package com.concert.app.interfaces.api.v1.concert;

import com.concert.app.domain.concert.ConcertService;
import com.concert.app.domain.concert.SelectConcertResult;
import com.concert.app.domain.concert.SelectConcertScheduleResult;
import com.concert.app.interfaces.api.common.CommonRes;
import com.concert.app.interfaces.api.v1.concert.res.ConcertRes;
import com.concert.app.interfaces.api.v1.concert.res.ConcertScheduleRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "콘서트 API", description = "콘서트 예매와 관련된 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/concerts")
public class ConcertController {
    private final ConcertService concertService;

    @GetMapping("/list")
    @Operation(summary = "예약 가능한 콘서트 일정 조회")
    public CommonRes<ConcertRes> selectConcert(
    ){
        SelectConcertResult concertList = concertService.selectConcertList();
        return CommonRes.success(ConcertRes.of(concertList));
    }

    @GetMapping("/details/{concertId}")
    @Operation(summary = "콘서트 상세 정보 조회")
    public CommonRes<ConcertScheduleRes> selectConcertSchedule(@PathVariable Long concertId) {
        SelectConcertScheduleResult concertScheduleList = concertService.selectConcertSchedule(concertId);
        return CommonRes.success(ConcertScheduleRes.of(concertScheduleList));
    }
}