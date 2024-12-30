package com.concert.app.domain.concert;

import java.time.LocalDate;
import java.util.List;

public interface ConcertRepository {
    List<Concert> findActiveConcerts(LocalDate now);
    Concert findByIdAndNotDeleted(Long concertId);
}