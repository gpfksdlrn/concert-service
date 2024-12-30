package com.concert.app.domain.concert;

import java.util.List;

public interface ConcertScheduleRepository {
    List<ConcertSchedule> findByConcertId(Long concertId);
}