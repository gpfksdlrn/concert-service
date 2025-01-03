package com.concert.infrastructure;

import com.concert.domain.ConcertSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertSchedule, Long> {
    List<ConcertSchedule> findByConcertIdAndScheduleAtAfter(Long concertId, LocalDateTime now);
}