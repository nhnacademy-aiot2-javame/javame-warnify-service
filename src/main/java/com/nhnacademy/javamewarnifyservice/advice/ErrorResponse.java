package com.nhnacademy.javamewarnifyservice.advice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@RequiredArgsConstructor
public class ErrorResponse {

    /**
     * 에러 생성 시간.
     */
    private final LocalDateTime timestamp;

    /**
     * 에러 코드.
     */
    private final int statusCode;

    /**
     * 에러.
     */
    private final String error;

    /**
     * 에러 메세지.
     */
    private final String message;

}
