package com.concert.app.domain.concert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertRepository concertRepository;

    // 등록되어 있는 콘서트 리스트 조회
    public SelectConcertResult selectConcertList() {
        List<Concert> concerts = concertRepository.findActiveConcerts(LocalDateTime.now());

        List<SelectConcertResult.Concert> resultConcerts = new ArrayList<>();
        for (Concert concert : concerts) {
            SelectConcertResult.Concert resultConcert = new SelectConcertResult.Concert(
                concert.getId(),
                concert.getTitle(),
                concert.getDescription(),
                concert.getLocation(),
                concert.getPlayStartAt().toString(),
                concert.getPlayEndAt() != null ? concert.getPlayEndAt().toString() : null,
                concert.getIsDeleted()
            );
            resultConcerts.add(resultConcert);
        }
        return new SelectConcertResult(resultConcerts);
    }
}