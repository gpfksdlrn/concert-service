package com.concert.infrastructure;

import com.common.exception.ApiException;
import com.common.exception.ExceptionCode;
import com.concert.domain.Concert;
import com.concert.domain.ConcertRepository;
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
