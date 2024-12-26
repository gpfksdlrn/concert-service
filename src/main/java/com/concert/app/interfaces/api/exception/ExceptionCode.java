package com.concert.app.interfaces.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
    // 암호화 에러
    ENCRYPTION_ERROR("500", "암호화 과정에서 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DECRYPTION_ERROR("500", "복호화 과정에서 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    GENERATION_ERROR("500", "키 생성 과정에서 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ENCRYPTION_KEY_INVALID("400", "암호화 키는 null 이거나 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST),
    ENCRYPTION_DATA_INVALID("400", "암호화할 데이터는 null 이거나 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST),
    DECRYPTION_DATA_INVALID("400", "복호화할 데이터는 null 이거나 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST),

    // 회원 관련
    DUPLICATED_EMAIL("400", "이미 가입된 이메일입니다.", HttpStatus.BAD_REQUEST),
    WRONG_ADMIN_TOKEN("403", "관리자 암호가 틀려 등록이 불가능합니다.", HttpStatus.UNAUTHORIZED),

    // JWT 에러
    TOKEN_INVALID("401", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("401", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),


    E403("403", "접근할 수 없습니다.", HttpStatus.FORBIDDEN),
    E404("404", "데이터를 조회할 수 없습니다.", HttpStatus.NOT_FOUND),
    E500("500", "알 수 없는 문제가 발생했습니다. 관리자에게 문의 부탁드립니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
