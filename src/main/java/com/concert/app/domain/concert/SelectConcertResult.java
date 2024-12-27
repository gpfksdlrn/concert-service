package com.concert.app.domain.concert;

import java.util.List;

// 콘서트 리스트 조회
public record SelectConcertResult(
        List<Concert> concerts
) {
    public record Concert(
            long id,              // 콘서트 ID
            String title,         // 콘서트 제목
            String description,   // 콘서트 설명
            String location,      // 콘서트 장소
            String playStartAt,   // 시작 시간
            String playEndAt,     // 종료 시간
            boolean isDeleted     // 삭제 여부
    ){
    }
}