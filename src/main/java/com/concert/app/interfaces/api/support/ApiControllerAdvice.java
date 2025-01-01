package com.concert.app.interfaces.api.support;

import com.concert.app.interfaces.api.common.CommonRes;
import com.concert.app.domain.exception.ApiException;
import com.concert.app.domain.exception.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class ApiControllerAdvice {
    // ApiException이 처리 되는 메서드
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<CommonRes<?>> handleCoreApiException(ApiException e) {
        switch (e.getLogLevel()) {
            case ERROR -> log.error("ApiException : {}", e.getMessage(), e);
            case WARN -> log.warn("ApiException : {}", e.getMessage(), e);
            default -> log.info("ApiException : {}", e.getMessage(), e);
        }

        // ExceptionCode 의 httpStatus 를 직접 사용
        HttpStatus httpStatus = e.getExceptionCode().getHttpStatus();
        return new ResponseEntity<>(CommonRes.error(e.getExceptionCode(), e.getData()), httpStatus);
    }

    // IllegalArgumentException (사용자가 잘못 된 인자를 넘겼을때) 처리 되는 메서드
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonRes<?>> handleCoreApiException(IllegalArgumentException e) {
        log.info("IllegalArgumentException : {}", e.getMessage(), e);
        return new ResponseEntity<>(CommonRes.error(e, BAD_REQUEST), BAD_REQUEST);
    }

    // NULL 에러가 처리되는 메서드
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<CommonRes<?>> handleCoreApiException(NullPointerException e) {
        log.info("NullPointException : {}", e.getMessage(), e);
        return new ResponseEntity<>(CommonRes.error(e, INTERNAL_SERVER_ERROR), INTERNAL_SERVER_ERROR);
    }

    // 최상위 에러가 처리되는 메서드
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonRes<?>> handleException(Exception e) {
        log.error("Exception : {}", e.getMessage(), e);
        return new ResponseEntity<>(CommonRes.error(ExceptionCode.E500, INTERNAL_SERVER_ERROR), INTERNAL_SERVER_ERROR);
    }
}
