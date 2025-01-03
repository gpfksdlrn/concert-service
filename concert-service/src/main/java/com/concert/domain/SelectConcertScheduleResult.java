package com.concert.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// 콘서트 리스트 조회
public record SelectConcertScheduleResult(
        long id,                    // 콘서트 ID
        String title,               // 콘서트 제목
        String description,         // 콘서트 설명
        String location,            // 콘서트 장소
        LocalDate playStartAt,      // 콘서트 일정 시작
        LocalDate playEndAt,        // 콘서트 일정 종료
        boolean isDelete,          // 삭제 여부
        List<Schedule> schedules    // 스케줄 리스트
) {
    public record Schedule(
            long id,                        // 스케줄 ID
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
            LocalDateTime scheduleAt,       // 콘서트 일시
            int runningTime,                // 관람 시간 (분 단위)
            int totalSeat,                  // 총 좌석 수
            int remainSeat,                 // 남은 좌석 수
            TotalSeatStatus totalSeatStatus // 좌석 상태 AVAILABLE, SOLD_OUT
    ){}
}