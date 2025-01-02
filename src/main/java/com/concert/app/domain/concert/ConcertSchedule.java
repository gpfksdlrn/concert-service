package com.concert.app.domain.concert;

import com.common.exception.ApiException;
import com.common.exception.ExceptionCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.logging.LogLevel;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "CONCERT_SCHEDULES")
public class ConcertSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "concert_id", nullable = false)
    private Long concertId;

    @Getter
    @Column(name = "schedule_at", nullable = false)
    private LocalDateTime scheduleAt;

    @Column(name = "running_time", nullable = false)
    private int runningTime;

    @Column(name = "total_seat", nullable = false)
    private int totalSeat;

    @Column(name = "remain_seat", nullable = false)
    private int remainSeat;

    @Column(name = "total_seat_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TotalSeatStatus totalSeatStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_delete", nullable = false)
    private Boolean isDelete = false;

    public void isSoldOutCheck() {
        if(this.totalSeatStatus != TotalSeatStatus.AVAILABLE) {
            throw new ApiException(ExceptionCode.CONCERT_SOLD_OUT, LogLevel.INFO);
        }
    }
}