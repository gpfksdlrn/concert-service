package com.concert.interfaces.res;

import com.concert.domain.SelectConcertScheduleResult;
import com.concert.domain.TotalSeatStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record ConcertScheduleRes(
        long id,
        String title,
        String description,
        String location,
        LocalDate playStartAt,
        LocalDate playEndAt,
        boolean isDelete,
        List<Schedule> schedules
) {
    public record Schedule(
            long id,
            LocalDateTime scheduleAt,
            int runningTime,
            int totalSeat,
            int remainSeat,
            TotalSeatStatus totalSeatStatus
    ){}

    public static ConcertScheduleRes of(SelectConcertScheduleResult result) {
        List<Schedule> scheduleList = new ArrayList<>();

        for(SelectConcertScheduleResult.Schedule schedule : result.schedules()) {
            scheduleList.add(new Schedule(
               schedule.id(),
               schedule.scheduleAt(),
               schedule.runningTime(),
               schedule.totalSeat(),
               schedule.remainSeat(),
               schedule.totalSeatStatus()
            ));
        }
        return new ConcertScheduleRes(
                result.id(),
                result.title(),
                result.description(),
                result.location(),
                result.playStartAt(),
                result.playEndAt(),
                result.isDelete(),
                scheduleList
        );
    }
}