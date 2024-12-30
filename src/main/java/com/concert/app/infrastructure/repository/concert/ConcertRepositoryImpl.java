package com.concert.app.infrastructure.repository.concert;

import com.concert.app.domain.concert.Concert;
import com.concert.app.domain.concert.ConcertRepository;
import com.concert.app.interfaces.api.exception.ApiException;
import com.concert.app.interfaces.api.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {
    private final ConcertJpaRepository jpaRepository;

    @Override
    public List<Concert> findActiveConcerts(LocalDate now) {
        return jpaRepository.findActiveConcerts(now);
    }

    @Override
    public Concert findByIdAndNotDeleted(Long concertId) {
        return jpaRepository.findByIdAndIsDeleteFalse(concertId)
                .orElseThrow(() -> new ApiException(ExceptionCode.E404, LogLevel.ERROR));
    }
}
