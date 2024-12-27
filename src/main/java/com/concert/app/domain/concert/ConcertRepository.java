package com.concert.app.domain.concert;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertRepository {
    List<Concert> findActiveConcerts(LocalDateTime now);
}