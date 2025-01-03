package com.concert.infrastructure;

import com.concert.domain.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
    @Query("SELECT c FROM Concert c WHERE c.isDelete = false AND (c.playEndAt IS NULL OR c.playEndAt >= :now)")
    List<Concert> findActiveConcerts(@Param("now") LocalDate now);

    Optional<Concert> findByIdAndIsDeleteFalse(Long concertId);
}