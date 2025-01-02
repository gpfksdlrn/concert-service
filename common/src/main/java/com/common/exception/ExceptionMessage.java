package com.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@JsonInclude(NON_NULL)
@NoArgsConstructor
public class ExceptionMessage {

    private String code;
    private String message;
    private Object data;

    public ExceptionMessage(Exception exception, HttpStatus httpStatus) {
        this.code = httpStatus.toString();
        this.message = exception.getMessage();
        this.data = null;
    }

    public ExceptionMessage(ExceptionCode exceptionCode, Object data) {
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMessage();
        this.data = data;
    }
}
