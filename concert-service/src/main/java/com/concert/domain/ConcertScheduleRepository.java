package com.concert.domain;

import java.util.List;

public interface ConcertScheduleRepository {
    List<ConcertSchedule> findByConcertId(Long concertId);
}