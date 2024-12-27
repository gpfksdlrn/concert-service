package com.concert.app.domain.concert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertRepository concertRepository;

    // 등록되어 있는 콘서트 리스트 조회
    public SelectConcertResult selectConcertList() {
        List<Concert> concerts = concertRepository.findActiveConcerts(LocalDate.now());

        List<SelectConcertResult.Concert> resultConcerts = new ArrayList<>();
        for (Concert concert : concerts) {
            SelectConcertResult.Concert resultConcert = new SelectConcertResult.Concert(
                concert.getId(),
                concert.getTitle(),
                concert.getDescription(),
                concert.getLocation(),
                concert.getPlayStartAt(),
                concert.getPlayEndAt() != null ? concert.getPlayEndAt() : null,
                concert.getIsDelete()
            );
            resultConcerts.add(resultConcert);
        }
        return new SelectConcertResult(resultConcerts);
    }
}