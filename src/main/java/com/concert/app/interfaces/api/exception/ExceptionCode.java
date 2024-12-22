package com.concert.app.interfaces.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
    TOKEN_INVALID("401", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("401", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),

    E403("403", "접근할 수 없습니다.", HttpStatus.FORBIDDEN),
    E404("404", "데이터를 조회할 수 없습니다.", HttpStatus.NOT_FOUND),
    E500("500", "알 수 없는 문제가 발생했습니다. 관리자에게 문의 부탁드립니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
