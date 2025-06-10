package com.nhnacademy.javamewarnifyservice.advice;

import com.nhnacademy.javamewarnifyservice.JavameWarnifyServiceApplication;
import com.nhnacademy.javamewarnifyservice.advice.exception.MemberListNotFound;
import com.nhnacademy.javamewarnifyservice.config.KSTTime;
import net.nurigo.sdk.message.exception.NurigoBadRequestException;
import net.nurigo.sdk.message.exception.NurigoUnknownException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import javax.mail.AuthenticationFailedException;

/**
 * 에러 핸들러.
 */
@RestControllerAdvice(basePackageClasses = JavameWarnifyServiceApplication.class)
public class WarnifyAdvice {

    /**
     * 이메일 발생시 인증이 실패할 경우 발생.
     * @param ex 실패 Exception
     * @return ErrorResponse
     */
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> emailAuthenticationFail(AuthenticationFailedException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                KSTTime.kstTimeNow(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * coolsms로 문자메세지 발송을 하는데 실패할 경우 발생.
     * @param ex coolsms Exception
     * @return ErrorResponse
     */
    @ExceptionHandler(NurigoBadRequestException.class)
    public ResponseEntity<ErrorResponse> smsUnknownException(NurigoUnknownException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                KSTTime.kstTimeNow(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * HttpClientErrorException 발생시 에러.
     * @param ex HttpClientErrorException
     * @return ErrorResponse 반환.
     */
    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ResponseEntity<ErrorResponse> httpClientErrorException(HttpClientErrorException.BadRequest ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                KSTTime.kstTimeNow(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * MemberNotFound 에러 발생시.
     * @param ex MemberNotFound
     * @return ErrorResponse 반환.
     */
    @ExceptionHandler(MemberListNotFound.class)
    public ResponseEntity<ErrorResponse> memberListNotFound(MemberListNotFound ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                KSTTime.kstTimeNow(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

}
