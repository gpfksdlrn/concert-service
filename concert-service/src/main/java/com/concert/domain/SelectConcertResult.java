package com.concert.domain;

import java.time.LocalDate;
import java.util.List;

// 콘서트 리스트 조회
public record SelectConcertResult(
        List<Concert> concerts
) {
    public record Concert(
            long id,                // 콘서트 ID
            String title,           // 콘서트 제목
            String description,     // 콘서트 설명
            String location,        // 콘서트 장소
            LocalDate playStartAt,  // 콘서트 일정 시작
            LocalDate playEndAt,    // 콘서트 일정 종료
            boolean isDelete        // 삭제 여부
    ){
    }
}