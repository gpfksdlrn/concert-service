package com.concert.app.infrastructure.repository.concert;

import com.concert.app.domain.concert.Concert;
import com.concert.app.domain.concert.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {
    private final ConcertJpaRepository jpaRepository;

    @Override
    public List<Concert> findActiveConcerts(LocalDateTime now) {
        return jpaRepository.findActiveConcerts(now);
    }
}
