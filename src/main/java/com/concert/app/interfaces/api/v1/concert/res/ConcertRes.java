package com.concert.app.interfaces.api.v1.concert.res;

import com.concert.app.domain.concert.SelectConcertResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record ConcertRes(
        List<Concert> concerts
) {
    public record Concert(
            long id,              // 콘서트 ID
            String title,         // 콘서트 제목
            String description,   // 콘서트 설명
            String location,      // 콘서트 장소
            LocalDate playStartAt,   // 시작 시간
            LocalDate playEndAt,     // 종료 시간
            boolean isDelete      // 삭제 여부
    ){}

    public static ConcertRes of(SelectConcertResult result) {
        List<Concert> concertList = new ArrayList<>();

        for(SelectConcertResult.Concert concert : result.concerts()) {
            concertList.add(new Concert(
                    concert.id(),
                    concert.title(),
                    concert.description(),
                    concert.location(),
                    concert.playStartAt(),
                    concert.playEndAt(),
                    concert.isDelete()
            ));
        }
        return new ConcertRes(concertList);
    }
}