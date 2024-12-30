package com.concert.app.infrastructure.repository.concert;

import com.concert.app.domain.concert.ConcertSchedule;
import com.concert.app.domain.concert.ConcertScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConcertScheduleRepositoryImpl implements ConcertScheduleRepository {
    private final ConcertScheduleJpaRepository jpaRepository;

    @Override
    public List<ConcertSchedule> findByConcertId(Long concertId) {
        LocalDateTime now = LocalDateTime.now();
        return jpaRepository.findByConcertIdAndScheduleAtAfter(concertId, now);
    }
}