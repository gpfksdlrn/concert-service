package com.concert.app.infrastructure.repository.concert;

import com.concert.app.domain.concert.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
    @Query("SELECT c FROM Concert c WHERE c.isDeleted = false AND (c.playEndAt IS NULL OR c.playEndAt > :now)")
    List<Concert> findActiveConcerts(@Param("now") LocalDateTime now);
}
