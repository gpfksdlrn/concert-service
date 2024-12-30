package com.concert.app.domain.concert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertRepository concertRepository;
    private final ConcertScheduleRepository concertScheduleRepository;

    // 등록되어 있는 콘서트 리스트 조회
    public SelectConcertResult selectConcertList() {
        List<Concert> concerts = concertRepository.findActiveConcerts(LocalDate.now());

        List<SelectConcertResult.Concert> resultConcerts = new ArrayList<>();
        for (Concert concert : concerts) {
            SelectConcertResult.Concert resultConcert = new SelectConcertResult.Concert(
                concert.getId(),
                concert.getTitle(),
                concert.getDescription(),
                concert.getLocation(),
                concert.getPlayStartAt(),
                concert.getPlayEndAt() != null ? concert.getPlayEndAt() : null,
                concert.getIsDelete()
            );
            resultConcerts.add(resultConcert);
        }
        return new SelectConcertResult(resultConcerts);
    }

    public SelectConcertScheduleResult selectConcertSchedule(Long concertId) {
        Concert concert = concertRepository.findByIdAndNotDeleted(concertId);

        List<ConcertSchedule> concertSchedules =  concertScheduleRepository.findByConcertId(concertId);

        List<SelectConcertScheduleResult.Schedule> schedules = new ArrayList<>();
        for (ConcertSchedule schedule : concertSchedules) {
            schedules.add(new SelectConcertScheduleResult.Schedule(
                    schedule.getId(),
                    schedule.getScheduleAt(),
                    schedule.getRunningTime(),
                    schedule.getTotalSeat(),
                    schedule.getRemainSeat(),
                    schedule.getTotalSeatStatus()
            ));
        }

        // SelectConcertScheduleResult 반환
        return new SelectConcertScheduleResult(
                concert.getId(),
                concert.getTitle(),
                concert.getDescription(),
                concert.getLocation(),
                concert.getPlayStartAt(),
                concert.getPlayEndAt(),
                concert.getIsDelete(),
                schedules
        );
    }
}